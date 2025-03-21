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

public class DataEncryptionRemovalPyprojectMigrationStep extends AbstractMigrationTest {

    @Given("a Pyproject file that has the data encryption dependencies")
    public void aPyprojectThatHasTheDataEncryptionDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPyprojectMigration", "pyproject.toml");
    }

    @Given("a Pyproject file that does not have the data encryption dependencies")
    public void aPyprojectThatDoesNotHaveTheDataDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPyprojectMigration", "skip-pyproject.toml");
    }

    @When("the 1.13.0 data encryption removal pyproject migration executes")
    public void DataEncryptionRemovalPyprojectMigrationExecutes() {
        DataEncryptionRemovalPyprojectMigration migration = new DataEncryptionRemovalPyprojectMigration();
        performMigration(migration);
    }

    @Then("the data encryption dependencies are removed from the Pyproject file")
    public void theDataEncryptionDependenciesAreRemovedFromPyproject() {
        assertTestFileMatchesExpectedFile("Data encryption dependencies have been removed from Pyproject file");
    }

    @Then("the data encryption removal pyproject migration is skipped")
    public void theDataEncryptionRemovalMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
