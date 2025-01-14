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

import java.io.FileNotFoundException;


public class SparkInfrastructureUniversalConfigServerSideDiffYAMLMigrationTest extends AbstractMigrationTest {
    @Given("spark infrastructure yaml file that doesn't have server-side diff annotation but has annotation block")
    public void aSparkInfrastructureYamlFileWithoutServerSideDiff()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/with-annotation/default-values.yaml");

    }

    @Given("spark infrastructure yaml file that doesn't have annotation block")
    public void aSparkInfrastructureYamlFileWithoutAnnotation()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/no-annotation/default-values.yaml");

    }

    @Given("spark infrastructure yaml file that has server-side diff annotation")
    public void aSparkInfrastructureYamlFileWithServerSideDiff()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/with-serverside-diff/default-values.yaml");

    }

    @When("the spark infrastructure configuration server-side diff migration executes")
    public void theSparkInfrastructureConfigurationServerSideDiffMigrationExecutes()
    {
        SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration migration = new SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration();
        performMigration(migration);
    }

    @Then("spark-infrastructure.yaml is updated to add server-side diff annotation.")
    public void theServerSideDiffAnnotationAdded() {
        assertMigrationSuccess();
        var updatedValue = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/with-annotation/updated-values.yaml");
        assertLinesMatch("Yaml file not updated to add server side diff annotation: " + testFile.getName(), testFile, updatedValue);
    }

    @Then("spark-infrastructure.yaml is updated to add server-side diff annotation with annotation block.")
    public void theServerSideDiffAnnotationAddedWithAnnotationBlock() {
        assertMigrationSuccess();
        var updatedValue = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/no-annotation/updated-values.yaml");
        assertLinesMatch("Yaml file not updated to add server side diff annotation: " + testFile.getName(), testFile, updatedValue);
    }

    @Then("the spark infrastructure configuration server-side diff migration is skipped")
    public void theMigrationisSkipped() throws FileNotFoundException {
        assertMigrationSkipped();
    }
}
