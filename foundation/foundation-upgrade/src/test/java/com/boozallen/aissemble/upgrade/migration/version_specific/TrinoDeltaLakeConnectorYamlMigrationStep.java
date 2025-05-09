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

public class TrinoDeltaLakeConnectorYamlMigrationStep extends AbstractMigrationTest {

    @Given("a trino yaml file that does not have the delta lake connector configured")
    public void aTrinoYamlFileThatDoesNotHaveTheDeltaLakeConnectorConfigured() {
        setTestFileToVersionMigration("TrinoDeltaLakeConnectorYamlMigration", "values.yaml");
    }

    @Given("a trino yaml file with the delta lake connector configured")
    public void aTrinoYamlFileWithTheDeltaLakeConnectorConfigured() {
        setTestFileToVersionMigration("TrinoDeltaLakeConnectorYamlMigration", "skip-values.yaml");
    }

    @When("the trino delta lake connector migration executes")
    public void theTrinoDeltaLakeConnectorMigrationExecutes() {
        TrinoDeltaLakeConnectorYamlMigration migration = new TrinoDeltaLakeConnectorYamlMigration();
        performMigration(migration);
    }

    @Then("the delta lake connector is configured in the trino yaml file")
    public void theDeltaLakeConnectorIsConfiguredInTheTrinoYamlFile() {
        assertTestFileMatchesExpectedFile("Data encryption dependencies have been removed from POM file");
    }

    @Then("the trino delta lake connector migration is skipped")
    public void theTrinoDeltaLakeConnectorMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
