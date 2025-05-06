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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FastapiDependencyRemovalValuesMigrationStep extends AbstractMigrationTest {

    @Given("a values.yaml file that has the aissemble-fastapi-chart configuration")
    public void aValuesYamlFileThatHasTheFastapiChartConfiguration() {
        setTestFileToVersionMigration("FastapiDependencyRemovalValuesMigration", "values.yaml");
    }

    @Given("a values.yaml file that does not have the aissemble-fastapi-chart configuration")
    public void aValuesYamlFileThatDoesNotHaveTheFastapiChartConfiguration() {
        setTestFileToVersionMigration("FastapiDependencyRemovalValuesMigration", "values-without-fastapi.yaml");
    }

    @When("the 1.13.0 aissemble-fastapi-chart removal migration executes")
    public void fastapiChartRemovalMigrationExecutes() {
        FastapiDependencyRemovalValuesMigration migration = new FastapiDependencyRemovalValuesMigration();
        performMigration(migration);
    }

    @Then("the aissemble-fastapi-chart configuration is removed from the values.yaml file")
    public void theFastapiChartConfigurationIsRemovedFromValuesYaml() {
        assertTestFileMatchesExpectedFile("aissemble-fastapi-chart configuration has been removed from values.yaml");
    }

    @Then("the aissemble-fastapi-chart removal migration is skipped")
    public void theFastapiChartRemovalMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}