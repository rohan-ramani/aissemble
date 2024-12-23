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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class InferenceDockerImageTagMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(InferenceDockerImageTagMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_11_0/InferenceDockerImageTagMigration/migration/ml-inference/%svalues-dev.yaml";

    @Given("an inference dev YAML file with no {string} defined")
    public void anInferenceDevYamlFileWithNoDefined(String configuration) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, String.format("no-%s-",configuration)));
    }

    @Given("an inference dev YAML file with expected image configuration defined")
    public void anInferenceDevYamlFileWithExpectedImageConfigDefined() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, ""));
    }

    @When("the 1.11.0 inference docker image tag migration executes")
    public void inferenceDockerImageTagMigrationExecutes() {
        InferenceDockerImageTagMigrationTest migration = new InferenceDockerImageTagMigrationTest();
        performMigration(migration);
    }

    @Then("the image configuration is added")
    public void theImageConfigIsAdded() {
        String testFilePath = testFile.getPath().replaceAll("migration", "validation");
        File validatedFile = new File(testFilePath);
        assertLinesMatch("The latest image tag configuration is removed.", validatedFile, testFile);
    }

    @Then("inference docker image migration is skipped")
    public void theMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    public class InferenceDockerImageTagMigrationTest extends InferenceDockerImageTagMigration {
        protected MavenProject getRootProject() {
            // root project
            MavenProject project = new MavenProject();
            project.setArtifactId("test-pipelines");
            project.setVersion("1.0.0-SNAPSHOT");

            MavenProject parentProject = new MavenProject();
            parentProject.setArtifactId("ml-pipeline");
            parentProject.setVersion("1.0.0-SNAPSHOT");

            // set ml project
            MavenProject mlProject = new MavenProject();
            mlProject.setArtifactId("ml-inference");
            mlProject.setParent(parentProject);
            Model model = new Model();
            model.setArtifactId("ml-inference");

            Plugin plugin = new Plugin();
            plugin.setArtifactId("fermenter-mda");
            plugin.setGroupId("org.technologybrewery.fermenter");

            // set plugin profile configuration
            Xpp3Dom profile = new Xpp3Dom("profile");
            profile.setValue("machine-learning-inference");

            Xpp3Dom configuration = new Xpp3Dom("configuration");
            configuration.addChild(profile);
            plugin.setConfiguration(configuration);

            Build build = new Build();
            build.setPlugins(Arrays.asList(plugin));

            model.setBuild(build);

            mlProject.setModel(model);

            project.setCollectedProjects(Arrays.asList(mlProject));
            return project;
        }
    }
}
