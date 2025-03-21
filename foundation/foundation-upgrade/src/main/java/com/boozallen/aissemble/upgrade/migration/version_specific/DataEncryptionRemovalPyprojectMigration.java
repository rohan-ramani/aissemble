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

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * This migration updates the pyproject.toml file to remove the data encryption dependencies that are no longer supported.
 */
public class DataEncryptionRemovalPyprojectMigration extends AbstractPomMigration {

    public static final String DATA_ENCRYPTION_DEPENDENCIES = "^aissemble-(foundation-encryption-policy|extensions-encryption-vault)-python\\s*=.*$";

    @Override
    protected boolean shouldExecuteOnFile(File pyproject) {
        try {
            return FileUtils.hasRegExMatch(DATA_ENCRYPTION_DEPENDENCIES, pyproject);
        } catch (IOException e) {
            throw new BatonException("Could not check pyproject.toml for data encryption dependencies: " + pyproject.getPath(), e);
        }
    }

    @Override
    protected boolean performMigration(File pyproject) {
        try {
            List<String> lines = Files.readAllLines(pyproject.toPath());
            List<String> updateLines = new ArrayList<>();
            boolean update = false;
            for (String line : lines) {
                if (line.matches(DATA_ENCRYPTION_DEPENDENCIES)) {
                    update = true;
                } else {
                    updateLines.add(line);
                }
            }
            if (update) {
                Files.write(pyproject.toPath(), updateLines);
                return true;
            }
        } catch (IOException e) {
            throw new BatonException("Failed to update data encryption dependencies in: " + pyproject.getPath(), e);
        }
        return false;
    }
}
