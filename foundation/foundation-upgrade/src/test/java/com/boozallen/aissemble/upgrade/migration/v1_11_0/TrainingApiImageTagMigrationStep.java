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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TrainingApiImageTagMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(TrainingApiImageTagMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_11_0/TrainingApiImageTagMigration/migration/%s/values.yaml";

    @Given("a training api yaml has latest image tag configuration under trainingApiConfigDev")
    public void aDockerModulePomFileWithFabric8PluginAndSkipConfiguration() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "perform"));
    }

    @Given("a training api yaml has no image tag configuration under trainingApiConfigDev")
    public void aDockerModulePomFileWithFabric8PluginButNoSkipConfiguration() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "skip"));
    }

    @When("the 1.11.0 training api image tag migration executes")
    public void enableMavenDockerBuildMigrationExecutes() {
        TrainingApiImageTagMigration migration = new TrainingApiImageTagMigration();
        performMigration(migration);
    }

    @Then("the image tag configuration is removed")
    public void theFabric8PluginManagementSkipConfigIsRemoved() {
        String testFilePath = testFile.getPath().replaceAll("migration", "validation");
        File validatedFile = new File(testFilePath);
        assertLinesMatch("The latest image tag configuration is removed.", validatedFile, testFile);
    }

    @Then("training api image tag migration is skipped")
    public void theMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}
