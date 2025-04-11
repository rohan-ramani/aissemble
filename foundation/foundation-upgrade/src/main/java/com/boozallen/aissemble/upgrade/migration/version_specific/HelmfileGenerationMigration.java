package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

/**
 * Migration to create the initial helmfile the archetype creates. If projects are upgrading to this version then
 * they will not be running the archetype generation and will not have the default helmfile to add manual actions too
 * . Because projects may choose not to use helmfile, we only want to generate this once and then deactivate it. If
 * the helmfile already exists then we want to not generate a new one and deactivate this migration.
 */
public class HelmfileGenerationMigration extends AbstractAissembleMigration {

    private static final Logger logger = LoggerFactory.getLogger(HelmfileGenerationMigration.class);
    private static final String BATON_KEY = "org.technologybrewery.baton:baton-maven-plugin";
    private static final String DEACTIVATED_TAG = "deactivateMigrations";
    private static final String MIGRATION_ID = "helmfile-generation-migration";
    private static final String VERSION_TAG_REGEX = "(.*)(\\$\\{archetypeVersion})";
    private static final String HELMFILE_TEMPLATE_PATH = "version_specific/helmfile.yaml";

    /**
     * Determines whether the migration should execute.
     * if the migration is not ignored, it should always execute against the root pom. If it is not ignored but the
     * helmfile already exists, it will skip generating a new one and add itself to the ignore list.
     *
     * @param file pom file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);

        // Check if the pom file is at the projects root level
        return model.getParent() == null || AISSEMBLE_PARENT.equals(model.getParent().getArtifactId());
    }

    /**
     * First checks if helmfile exists. If not, initialize it. Then add this migration to the deactivated list
     *
     * @param file pom file to migrate
     * @return true if migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        createHelmfileIfNotExist();
        return addMigrationToIgnoreList(file);
    }

    private void createHelmfileIfNotExist() throws BatonException {
        String helmfileLocation = getRootProject().getBasedir().getPath() + "/helmfile.yaml";
        File file = new File(helmfileLocation);
        if (file.exists()) {
            logger.info("Helmfile already found. Skipping creation and ignoring migration");
        } else {
            try {
                InputStream initialHelmfile = getClass().getClassLoader().getResourceAsStream(HELMFILE_TEMPLATE_PATH);
                if (initialHelmfile == null) {
                    throw new BatonException("Could not find the helmfile template");
                }
                File helmfile = new File(helmfileLocation);
                FileUtils.copyInputStreamToFile(initialHelmfile, helmfile);
                updateArtifactId(helmfile);
            } catch (IOException e) {
                throw new BatonException("Failed to instantiate the helmfile.yaml", e);
            }
            logger.info("Initialized root helmfile.yaml");
        }

    }

    private void updateArtifactId(File file) {
        try (BufferedReader valuesFile = new BufferedReader((new FileReader(file)))) {
            String line;
            Pattern pattern = Pattern.compile(VERSION_TAG_REGEX, Pattern.MULTILINE);

            while ((line = valuesFile.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String substitution = "$1" + getAissembleVersion();
                    replaceInFile(file, VERSION_TAG_REGEX, substitution);
                }
            }
        } catch (IOException e) {
            throw new BatonException("Unable to modify the helmfile's artifact ID.", e);
        }
    }

    private boolean addMigrationToIgnoreList(File file) {
        logger.info("Deactivating Helmfile generation migration.");
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
            Xpp3Dom deactivated = config.getChild(DEACTIVATED_TAG);
            if (deactivated == null) {
                modifications = addDeactivatedMigrations(baton.getLocation("configuration" + END));
            } else {
                modifications = deactivateThisMigration(deactivated);
            }
        }
        PomHelper.writeModifications(file, modifications.finalizeMods());
        return true;
    }

    private PomModifications addConfig(Plugin baton) {
        PomModifications modifications = new PomModifications();
        modifications.add(new PomModifications.Insertion(
                baton.getLocation("dependencies" + START),
                4,
                HelmfileGenerationMigration::getConfig));
        return modifications;
    }

    private PomModifications addDeactivatedMigrations(InputLocation configEnd) {
        PomModifications modifications = new PomModifications();
        modifications.add(new PomModifications.Insertion(
                configEnd,
                4,
                HelmfileGenerationMigration::getDisableConfig));
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
                    HelmfileGenerationMigration::getDisableTag));
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
}
