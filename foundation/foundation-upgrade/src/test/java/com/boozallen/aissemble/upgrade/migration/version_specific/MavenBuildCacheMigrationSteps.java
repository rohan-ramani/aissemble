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

public class MavenBuildCacheMigrationSteps extends AbstractMigrationTest {

    @Given("a Maven build cache config with max builds set higher than one")
    public void aMavenBuildCacheConfigWithMaxBuildsSetHigherThanOne() {
        setTestFileToVersionMigration("MavenBuildCacheMigration", "multiple-cached-builds.xml");
    }

    @Given("a Maven build cache config with max builds set to one")
    public void aMavenBuildCacheConfigWithMaxBuildsSetToOne() {
        setTestFileToVersionMigration("MavenBuildCacheMigration", "one-max-build.xml");
    }
    @When("the 1.13.0 Maven build cache migration executes")
    public void theMavenBuildCacheMigrationExecutes() {
        performMigration(new MavenBuildCacheMigration());
    }

    @Then("the config is updated to set max builds to one")
    public void theConfigIsUpdatedToSetMaxBuildsTo() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("<maxBuilds> not correctly updated to 1!");
    }

    @Then("the build cache migration is skipped")
    public void theBuildCacheMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
