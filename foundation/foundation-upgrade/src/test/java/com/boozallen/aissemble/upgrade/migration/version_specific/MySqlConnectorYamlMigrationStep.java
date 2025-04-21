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

public class MySqlConnectorYamlMigrationStep extends AbstractMigrationTest {

    @Given("a spark-application values file that references mysql-connector-java in sparkApp.spec.deps.jars")
    public void sSparkApplicationValuesFileRefsToMySqlConnectorJavaInSparkAppDepsJars() {
        setTestFileToVersionMigration("MySqlConnectorYamlMigration", "values.yaml");
    }

    @Given("a spark-application values file that already references mysql-connector-j in spec.deps.jars")
    public void sSparkApplicationValuesFileRefsToMySqlConnectorJInSparkAppDepsJars() {
        setTestFileToVersionMigration("MySqlConnectorYamlMigration", "skip-values.yaml");
    }

    @When("the MySqlConnector yaml migration executes")
    public void mySqlConnectorYamlMigrationExecutes() {
        MySqlConnectorYamlMigration migration = new MySqlConnectorYamlMigration();
        performMigration(migration);
    }

    @Then("the mysql-connector-java jar coordinates are updated to mysql-connector-j jar")
    public void thePomFileIsUpdated() {
        assertTestFileMatchesExpectedFile("The values yaml file is not updated correctly");
    }

    @Then("the MySqlConnector migration is skipped")
    public void theMySqlConnectorMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
