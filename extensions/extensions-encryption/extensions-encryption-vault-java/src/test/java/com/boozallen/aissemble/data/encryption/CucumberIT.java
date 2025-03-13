package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Configuration for running Cucumber integration tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/specifications",
        // TODO: re-enable or remove when vault encryption is ready or removed
        // Disable cucumber report for integration test since we only have vault for integration test, which is disabled for now
        // plugin = {"json:target/cucumber-reports/cucumber.json"},
        tags = "@integration and not @disabled")
public class CucumberIT {

}
