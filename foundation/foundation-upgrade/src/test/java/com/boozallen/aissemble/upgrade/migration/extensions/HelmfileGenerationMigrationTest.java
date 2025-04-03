package com.boozallen.aissemble.upgrade.migration.extensions;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.upgrade.migration.version_specific.HelmfileGenerationMigration;

public class HelmfileGenerationMigrationTest extends HelmfileGenerationMigration {

    private static final Path TEST_FILES_FOLDER = Paths.get("target", "test-classes", "test-files");

    @Override
    protected MavenProject getRootProject() {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId("build-parent");

        MavenProject project = new MavenProject();
        project.setArtifactId("test-pipelines");
        project.setParent(parentAissembleProject);
        project.setFile(getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "pom.xml").toString()));

        return project;
    }

    @Override
    public String getAissembleVersion() {
        return "1.12.1";
    }

    protected static File getTestFile(String subPath) {
        if (subPath.startsWith(File.separator)) {
            subPath = subPath.substring(1);
        }
        File testFile = TEST_FILES_FOLDER.resolve(subPath).toFile();
        File dir = testFile.getParentFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Parent directory of test file is already a regular file: " + dir);
        }
        return testFile;
    }
}
