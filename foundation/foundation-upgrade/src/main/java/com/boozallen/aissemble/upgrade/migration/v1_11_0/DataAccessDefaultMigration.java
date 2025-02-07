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
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;

/**
 * For any Record metamodel JSON files that are using the default value of Data Access (i.e. not specifying), updates
 * the JSON to explicitly set Data Access to enabled (the old default).
 */
public class DataAccessDefaultMigration extends AbstractAissembleMigration {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            if (file.getName().endsWith(".json")) {
                JsonNode json = objectMapper.readTree(file);
                if (!json.has("dataAccess")) {
                    return true;
                } else {
                    JsonNode dataAccess = json.get("dataAccess");
                    return dataAccess.isObject() && !dataAccess.has("enabled");
                }
            }
        } catch (IOException e) {
            throw new BatonException("Failed to parse JSON: " + file.getName(), e);
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
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
