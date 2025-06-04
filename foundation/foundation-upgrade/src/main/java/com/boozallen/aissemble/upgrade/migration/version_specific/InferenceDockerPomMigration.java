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

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

public class InferenceDockerPomMigration extends AbstractContainerizeMigration {

    public static final String AISSEMBLE_INFERENCE_DOCKER = "aissemble-inference-docker";

    /**
     * Determines if the migration should be executed. Will return true if the inference docker pom does not use the
     * Habushu containerization goal
     *
     * @param file file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        MavenProject mavenProject = getMavenProject();
        return isDockerBuildPackage(mavenProject) && containsFermenterProfile(mavenProject, AISSEMBLE_INFERENCE_DOCKER)
                && !containsHabushuContainerizeGoal(mavenProject);
    }

    /**
     * Performs the migration. Inserts the habushu-maven-plugin into the projects plugin list
     *
     * @param file POM file to insert the plugin into
     * @return true if the migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        detectAndSetIndent(file);
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications pomModifications = new PomModifications();

        // Safe to assume the pom has build and plugins because "shouldExecuteOnFile" checks the fermenter plugin
        final String insertContent = habushuPluginWithContainerizationGoal(3);
        pomModifications.add(new PomModifications.Insertion(model.getBuild().getLocation("plugins" + END),
                3, content -> insertContent));
        return PomHelper.writeModifications(file, pomModifications.finalizeMods());
    }
}