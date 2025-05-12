package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.extensions.HelmfileVersionMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmFileVersionMigrationSteps extends AbstractMigrationTest {

    @Given("a projects helmfile values has an out of date aissemble version")
    public void aProjectsHelmfileValuesHasAnOutOfDateAissembleVersion() {
        setTestFileToBaseMigration("HelmfileVersionMigration", "values-helmfile.yaml.gotmpl");
    }

    @Given("a projects helmfile values has the desired aissemble version")
    public void aProjectsHelmfileValuesHasTheDesiredAissembleVersion() {
        setTestFileToBaseMigration("HelmfileVersionMigration", "values-skip-helmfile.yaml.gotmpl");
    }

    @When("the helmfile version migration is performed")
    public void theHelmfileVersionMigrationIsPerformed() {
        performMigration(new HelmfileVersionMigrationTest());
    }

    @Then("the helmfile value is update")
    public void theHelmfileValueIsUpdate() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("aiSSEMBLE version for the helmfile values was not correctly updated");
    }

    @Then("the migration is not performed")
    public void theMigrationIsNotPerformed() {
        assertMigrationSkipped();
    }
}
