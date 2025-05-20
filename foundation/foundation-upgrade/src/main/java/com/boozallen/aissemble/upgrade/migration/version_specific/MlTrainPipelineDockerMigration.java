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
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.apache.commons.lang3.StringUtils.repeat;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;


/**
 * Migration that updates the POM files for machine learning pipelines with a train step to use Habushu's containerize-dependencies goal.
 */
public class MlTrainPipelineDockerMigration extends AbstractPomMigration {
    protected static final Logger logger = LoggerFactory.getLogger(MlTrainPipelineDockerMigration.class);
    private static final String DOCKER_BUILD_TYPE = "docker-build";
    private static final String DOCKERIZE = "dockerize";
    private static final String HABUSHU_GROUP_ID = "org.technologybrewery.habushu";
    private static final String HABUSHU_ARTIFACT_ID = "habushu-maven-plugin";
    private static final String FERMENTER_MDA_PLUGIN_ID = "org.technologybrewery.fermenter:fermenter-mda";
    private static final String AISSEMBLE_TRAINING_DOCKER = "aissemble-training-docker";
    private static final String CONTAINERIZE_DEPS_GOAL = "containerize-dependencies";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        // package/type needs to be docker-build
        MavenProject mavenProject = getMavenProject();
        if (!StringUtils.equals(DOCKER_BUILD_TYPE, mavenProject.getModel().getPackaging())) {
            return false;
        }

        return isMlTrainingDockerPom(mavenProject) && !containsHabushuContainerizeGoal(mavenProject);
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        detectAndSetIndent(file);
        PomModifications modifications = new PomModifications();

        String content = getHabushuPluginConfiguration();
        modifications.add(new PomModifications.Insertion(model.getBuild().getLocation("plugins" + END), 3, ignore -> content));

        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    private boolean containsHabushuContainerizeGoal(MavenProject mavenProject) {
        Plugin habushuPlugin = mavenProject.getPlugin(HABUSHU_GROUP_ID + ":" + HABUSHU_ARTIFACT_ID);
        if (habushuPlugin != null) {
            for (PluginExecution execution : habushuPlugin.getExecutions()) {
                if (DOCKERIZE.equals(execution.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isMlTrainingDockerPom(MavenProject mavenProject){
        getMavenProject().getBuildPlugins();
        Plugin fermenterMdaPlugin = mavenProject.getPlugin(FERMENTER_MDA_PLUGIN_ID);

        if (fermenterMdaPlugin == null) {
            return false;
        }

        Object rawConfig = fermenterMdaPlugin.getConfiguration();
        if (!(rawConfig instanceof Xpp3Dom)){
            return false;
        }
        Xpp3Dom configuration = (Xpp3Dom) rawConfig;
        for (Xpp3Dom child : configuration.getChildren()) {
            if (AISSEMBLE_TRAINING_DOCKER.equals(child.getValue())) {
                return true;
            }
        }
        return false;
    }

    private String getHabushuPluginConfiguration() {
        return repeat(indent, 3) + "<plugin>\n" +
                repeat(indent, 4) + "<groupId>" + HABUSHU_GROUP_ID + "</groupId>\n" +
                repeat(indent, 4) + "<artifactId>" + HABUSHU_ARTIFACT_ID + "</artifactId>\n" +
                repeat(indent, 4) + "<executions>\n" +
                repeat(indent, 5) + "<execution>\n" +
                repeat(indent, 6) + "<id>" + DOCKERIZE + "</id>\n" +
                repeat(indent, 6) + "<goals>\n" +
                repeat(indent, 7) + "<goal>" + CONTAINERIZE_DEPS_GOAL + "</goal>\n" +
                repeat(indent, 6) + "</goals>\n" +
                repeat(indent, 6) + "<phase>prepare-package</phase>\n" +
                repeat(indent, 6) + "<configuration>\n" +
                repeat(indent, 7) + "<dockerfile>src/main/resources/docker/Dockerfile</dockerfile>\n" +
                repeat(indent, 7) + "<dockerBuilderBase>${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-nvidia:${VERSION_AISSEMBLE}</dockerBuilderBase>\n" +
                repeat(indent, 7) + "<dockerFinalBase>${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-nvidia:${VERSION_AISSEMBLE}</dockerFinalBase>\n" +
                repeat(indent, 6) + "</configuration>\n" +
                repeat(indent, 5) + "</execution>\n" +
                repeat(indent, 4) + "</executions>\n" +
                repeat(indent, 3) + "</plugin>\n";
    }
}
