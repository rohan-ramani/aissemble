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

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.upgrade.migration.version_specific.HelmfileDeploymentScriptMigration;

public class HelmfileDeploymentScriptMigrationTest extends HelmfileDeploymentScriptMigration {


    @Override
    protected MavenProject getRootProject() {
        MavenProject parentAissembleProject = new MavenProject();
        parentAissembleProject.setArtifactId("build-parent");

        MavenProject project = new MavenProject();
        project.setArtifactId("test-project");
        project.setParent(parentAissembleProject);
        project.setName("test-project");
        Scm scm = new Scm();
        scm.setUrl("test.com/test-project");
        scm.setTag("HEAD");
        project.setScm(scm);

        return project;
    }
}
