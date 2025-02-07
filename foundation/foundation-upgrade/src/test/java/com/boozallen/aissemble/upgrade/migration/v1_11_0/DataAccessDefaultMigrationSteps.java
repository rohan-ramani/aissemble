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

public class DataAccessDefaultMigrationSteps extends AbstractMigrationTest {
    @Given("a record with dataAccess.enabled set explicitly")
    public void aRecordWithDataAccessEnabledSetExplicitly() {
        setTestFileToVersionMigration("DataAccessDefaultMigration", "ExplicitRecord.json");
    }

    @Given("a record with dataAccess undefined")
    public void aRecordWithDataAccessUndefined() {
        setTestFileToVersionMigration("DataAccessDefaultMigration", "UndefinedDataAccessRecord.json");
    }

    @Given("a record with dataAccess defined but enabled undefined")
    public void aRecordWithDataAccessDefinedButEnabledUndefined() {
        setTestFileToVersionMigration("DataAccessDefaultMigration", "UndefinedEnabledRecord.json");
    }

    @When("the data access default migration executes")
    public void theDataAccessDefaultMigrationExecutes() {
        DataAccessDefaultMigration migration = new DataAccessDefaultMigration();
        performMigration(migration);
    }

    @Then("the data access default migration is skipped")
    public void theDataAccessDefaultMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    @Then("the record is updated to set dataAccess.enabled to true")
    public void theRecordIsUpdatedToSetDataAccessEnabledToTrue() {
        assertTestFileMatchesExpectedFile("Data Access not added correctly to record file");
    }
}
