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

import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

import com.boozallen.aissemble.upgrade.migration.version_specific.HabushuMonorepoDependencyMigration;

/**
 * HabushuMonorepoDependencyMigrationTest
 */
public class HabushuMonorepoDependencyMigrationTest extends HabushuMonorepoDependencyMigration {
    private static final String TEST_GROUPID = "com.boozallen.test";
    public static final String TEST_VERSION = "1.0.0-SNAPSHOT";

    private List<MavenProject> projects = new ArrayList<>();

    public void addProject(String artifactId, String packaging) {
        MavenProject project = new MavenProject();
        project.setGroupId(TEST_GROUPID);
        project.setArtifactId(artifactId);
        project.setVersion(TEST_VERSION);
        project.setPackaging(packaging);
        projects.add(project);
    }

    @Override
    public void setMavenProject(MavenProject project) {
        super.setMavenProject(project);
        projects.add(project);
    }

    @Override
    protected MavenProject getRootProject() {
        return new MavenProject() {
            @Override
            public List<MavenProject> getCollectedProjects() {
                return projects;
            }
        };
    }
}

