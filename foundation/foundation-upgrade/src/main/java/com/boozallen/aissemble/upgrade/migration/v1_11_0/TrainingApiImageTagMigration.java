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
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This migration will remove the `image_tage=latest` configuration from the model-training-api helm configuration
 * to default the image tag the project version
 */
public class TrainingApiImageTagMigration extends AbstractAissembleMigration {

    public static final String IMAGE_TAG_LATEST = "image_tag=latest";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return Files.readString(file.toPath()).contains(IMAGE_TAG_LATEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        int latestImageTagIndex = -1;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.equals(IMAGE_TAG_LATEST)) {
                    latestImageTagIndex = i;
                    break;
                }
            }
            if (latestImageTagIndex > 0) {
                lines.remove(latestImageTagIndex);
                Files.write(file.toPath(), lines);
            }
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to remove `image_tag=latest` config in: " + file.getPath(), e);
        }
    }
}
