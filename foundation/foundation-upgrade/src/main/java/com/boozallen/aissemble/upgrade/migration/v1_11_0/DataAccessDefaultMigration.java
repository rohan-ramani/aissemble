package com.boozallen.aissemble.upgrade.migration.v1_11_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

/**
 * For any Record metamodel JSON files that are using the default value of Data Access (i.e. not specifying), updates
 * the JSON to explicitly set Data Access to enabled (the old default).
 */
public class DataAccessDefaultMigration extends AbstractAissembleMigration {

    public static final String BATON_KEY = "org.technologybrewery.baton:baton-maven-plugin";
    public static final String DEACTIVATED_TAG = "deactivateMigrations";
    public static final String MIGRATION_ID = "data-access-default-migration";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (file.getName().endsWith(".json")) {
            try {
                JsonNode json = objectMapper.readTree(file);
                if (!json.has("dataAccess")) {
                    return true;
                } else {
                    JsonNode dataAccess = json.get("dataAccess");
                    return dataAccess.isObject() && !dataAccess.has("enabled");
                }
            } catch (IOException e) {
                throw new BatonException("Failed to parse JSON: " + file.getName(), e);
            }
        } else {
            Model model = PomHelper.getLocationAnnotatedModel(file);
            Build build = model.getBuild();
            if (build == null) {
                return true;
            }
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            if (plugins == null) {
                return true;
            }
            Plugin baton = plugins.get(BATON_KEY);
            if (baton == null) {
                return true;
            }
            Xpp3Dom config = (Xpp3Dom) baton.getConfiguration();
            if (config == null) {
                return true;
            }
            Xpp3Dom deactivated = config.getChild(DEACTIVATED_TAG);
            if (deactivated == null) {
                return true;
            }
            String inlineValues = deactivated.getValue();
            if (inlineValues != null && inlineValues.contains(MIGRATION_ID)) {
                return false;
            }
            for (Xpp3Dom eachDeactivated : deactivated.getChildren()) {
                if (MIGRATION_ID.equals(eachDeactivated.getValue())) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected boolean performMigration(File file) {
        if (file.getName().endsWith(".json")) {
            return migrateRecordJson(file);
        } else {
            return migrateRootPom(file);
        }
    }

    private boolean migrateRecordJson(File file) {
        try {
            String sample = Files.lines(file.toPath())
                    .filter(line -> line.contains("\"name\""))
                    .findAny()
                    .orElse("  .\n");
            String content = sample.trim();
            String indent = sample.substring(0, sample.indexOf(content));
            String eol = "\n";
            JsonNode json = objectMapper.readTree(file);
            json.withObjectProperty("dataAccess")
                    .put("enabled", true);
            objectMapper.writer(new AissembleJsonPrinter(indent, eol))
                    .writeValue(file, json);
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to update JSON file", e);
        }
    }

    private boolean migrateRootPom(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        Plugin baton = model.getBuild().getPluginsAsMap().get(BATON_KEY);
        if (baton == null) {
            throw new BatonException("Failed to find Baton plugin configuration in root POM");
        }
        PomModifications modifications;
        Xpp3Dom config = (Xpp3Dom) baton.getConfiguration();
        if (config == null) {
            modifications = addConfig(baton);
        } else {
            Xpp3Dom deactivated = config.getChild("deactivateMigrations");
            if (deactivated == null) {
                modifications = addDeactivatedMigrations(baton.getLocation("configuration" + END));
            } else {
                modifications = deactivateThisMigration(deactivated);
            }
        }
        PomHelper.writeModifications(file, modifications.finalizeMods());
        return false;
    }

    private PomModifications addConfig(Plugin baton) {
        PomModifications modifications = new PomModifications();
        modifications.add(new PomModifications.Insertion(
                baton.getLocation("dependencies" + START),
                4,
                DataAccessDefaultMigration::getConfig));
        return modifications;
    }

    private PomModifications addDeactivatedMigrations(InputLocation configEnd) {
        PomModifications modifications = new PomModifications();
        modifications.add(new PomModifications.Insertion(
                configEnd,
                4,
                DataAccessDefaultMigration::getDisableConfig));
        return modifications;
    }

    private PomModifications deactivateThisMigration(Xpp3Dom deactivated) {
        if (deactivated.getValue() != null) {
            return deactivateThisMigrationInline(deactivated);
        } else {
            PomModifications modifications = new PomModifications();
            InputLocation startTag = (InputLocation) deactivated.getInputLocation();
            modifications.add(new PomModifications.Insertion(
                    new InputLocation(
                            startTag.getLineNumber() + 1,
                            startTag.getColumnNumber(),
                            startTag.getSource()),
                    5,
                    DataAccessDefaultMigration::getDisableTag));
            return modifications;
        }
    }

    private PomModifications deactivateThisMigrationInline(Xpp3Dom deactivated) {
        PomModifications modifications = new PomModifications();
        InputLocation start = (InputLocation) deactivated.getInputLocation();
        String disabledList = deactivated.getValue();
        InputLocation end = new InputLocation(
                start.getLineNumber(),
                start.getColumnNumber() + disabledList.length(),
                start.getSource());
        modifications.add(new PomModifications.Replacement(
                start,
                end,
                5,
                i -> disabledList + "," + MIGRATION_ID));
        return modifications;
    }

    private static String getConfig(String i) {
        return StringUtils.repeat(i, 4) + "<configuration>\n" +
                getDisableConfig(i) +
                StringUtils.repeat(i, 4) + "</configuration>\n";
    }

    private static String getDisableConfig(String i) {
        return StringUtils.repeat(i, 5) + "<deactivateMigrations>\n" +
               getDisableTag(i) +
               StringUtils.repeat(i, 5) + "</deactivateMigrations>\n";
    }

    private static String getDisableTag(String i) {
        return StringUtils.repeat(i, 6) + "<deactivateMigration>" + MIGRATION_ID + "</deactivateMigration>\n";
    }

    private static class AissembleJsonPrinter extends DefaultPrettyPrinter {
        private final String eol;
        private final String indent;

        public AissembleJsonPrinter(String indent, String eol) {
            super(DefaultPrettyPrinter.DEFAULT_SEPARATORS.withObjectFieldValueSpacing(Separators.Spacing.AFTER));
            this.indent = indent;
            this.eol = eol;
            this._arrayIndenter = new DefaultIndenter(indent, eol);
            this._objectIndenter = new DefaultIndenter(indent, eol);
        }

        public AissembleJsonPrinter(AissembleJsonPrinter printer) {
            super(printer);
            this.indent = printer.indent;
            this.eol = printer.eol;
            this._arrayIndenter = new DefaultIndenter(indent, eol);
            this._objectIndenter = new DefaultIndenter(indent, eol);
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new AissembleJsonPrinter(this);
        }
    }
}
