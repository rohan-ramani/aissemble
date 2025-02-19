package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * aiSSEMBLE::Test::MDA::Data Delivery Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.technologybrewery.krausening.Krausening;

import static org.junit.Assert.assertNull;

/**
 * Implementation steps for encryption.feature
 */
public class EncryptionTest {
    private String defaultKrauseningBaseDir;
    private NativeInboundAndOutbound pipeline;
    private Exception exception;
    private Krausening krausening = Krausening.getInstance();

    @Given("a pipeline with an inbound data type")
    public void a_pipeline_with_an_inbound_data_type() {
        this.pipeline = new NativeInboundAndOutbound();
    }

    @Given("the policies location property is not defined")
    public void the_policies_location_property_is_not_defined() {
        // Get the current krausening base dir for restoring after the test
        this.defaultKrauseningBaseDir = System.getProperty("KRAUSENING_BASE");

        // Force krausening to reload with the new base dir
        System.setProperty("KRAUSENING_BASE", "invalid/path");
        this.krausening.loadProperties();
    }

    @When("the check and apply encryption method is called")
    public void the_check_and_apply_encryption_method_is_called() {
        try {
            this.pipeline.checkAndApplyEncryptionPolicy(null);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("the method completes without applying encryption")
    public void the_method_completes_without_applying_encryption() {
        assertNull("An exception was thrown", this.exception);

        // Restore the krausening base dir to the value from before the test
        System.setProperty("KRAUSENING_BASE", this.defaultKrauseningBaseDir);
        this.krausening.loadProperties();
    }
}
