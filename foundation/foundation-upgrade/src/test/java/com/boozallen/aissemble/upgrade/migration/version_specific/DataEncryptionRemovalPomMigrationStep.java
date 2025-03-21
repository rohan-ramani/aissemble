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

public class DataEncryptionRemovalPomMigrationStep extends AbstractMigrationTest {

    @Given("a POM file that has the data encryption dependencies")
    public void aPomFileThatHasTheDataEncryptionDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPomMigration", "pom.xml");
    }

    @Given("a POM file that does not have the data encryption dependencies")
    public void aPomFileThatDoesNotHaveTheDataDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPomMigration", "skip-pom.xml");
    }

    @When("the 1.13.0 data encryption removal pom migration executes")
    public void DataEncryptionRemovalPomMigrationExecutes() {
        DataEncryptionRemovalPomMigration migration = new DataEncryptionRemovalPomMigration();
        performMigration(migration);
    }

    @Then("the data encryption dependencies are removed from the POM file")
    public void theDataEncryptionDependenciesAreRemovedFromPomFile() {
        assertTestFileMatchesExpectedFile("Data encryption dependencies have been removed from POM file");
    }

    @Then("the data encryption removal pom migration is skipped")
    public void theDataEncryptionRemovalMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
