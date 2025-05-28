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

import com.boozallen.aissemble.upgrade.migration.version_specific.SparkBomDependencyMigration;

import org.apache.maven.project.MavenProject;

import java.io.File;

public class SparkBomDependencyMigrationTest extends SparkBomDependencyMigration {

    public SparkBomDependencyMigrationTest(File testPom) {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId(AISSEMBLE_PARENT);
        parentAissembleProject.setGroupId("com.boozallen.aissemble");

        MavenProject project = new MavenProject();
        project.setArtifactId("simple-project");
        project.setParent(parentAissembleProject);
        project.setFile(testPom);
        setMavenProject(project);
    }
}
