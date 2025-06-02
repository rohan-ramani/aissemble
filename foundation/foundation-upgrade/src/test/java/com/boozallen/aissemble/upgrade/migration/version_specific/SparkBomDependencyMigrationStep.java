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
import com.boozallen.aissemble.upgrade.migration.extensions.SparkBomDependencyMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class SparkBomDependencyMigrationStep extends AbstractMigrationTest {

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency")
    public void aPomFileWithoutAissembleSparkBom() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom.xml");
    }

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency with no spark dependency")
    public void aPomFileWithoutAissembleSparkBomAndNoSparkDependency() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom-no-spark.xml");
    }

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency with an existing dependencyManagement section")
    public void aPomFileWithoutAissembleSparkBomAndExistingDependencyManagementSection() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom-dependency-management-exists.xml");
    }

    @Given("contains a spark dependency")
    public void containsSparkDependency() {
        // Already covered by the test POM file content
    }

    @Given("includes aissemble build-parent in its hierarchy")
    public void includesAissembleBuildParent() {
        // Already covered by the test POM file content
    }

    @When("the aissemble-spark-bom pom migration executes")
    public void runMigration() {
        performMigration(new SparkBomDependencyMigrationTest(testFile));
    }

    @Then("the aissemble-spark-bom dependency is added to the POM file")
    public void aissembleSparkBomAdded() {
        assertTestFileMatchesExpectedFile("aissemble-spark-bom dependencyManagement block was not added as expected");
    }

    @Given("doesn't contain a spark dependency")
    public void doesNotContainSparkDependency() {
        // Already covered by the test POM file content
    }

    @Then("the migration is skipped")
    public void migrationIsSkipped() {
        assertMigrationSkipped();
    }
}
