package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA Patterns::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aiops.mda.pattern.dictionary.State;
import com.boozallen.aiops.mda.pattern.dictionary.Zipcode;
import com.boozallen.aiops.mda.pattern.record.Address;
import com.boozallen.aiops.mda.pattern.record.PersonWithMToOneRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToMRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToOneRelation;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test record generation for relation-validation.feature
 */
public class RelationValidationTest {

    private final String VALID_ZIPCODE = "12345-6789";
    private final String VALID_STATE = "NY";
    private final List<String> RANDOM_STREETS = List.of("123 ABC St.", "1st Street");
    private final List<String> RANDOM_CITIES = List.of("New York", "New York");
    private Map<String, TestPerson> relationClassNameMap;
    private TestPerson person;
    private String multiplicity;
    private Exception exception;
    private SparkSession sparkSession;

    @Before("@relation-validation")
    public void preSetupForTest() {
        exception = null;
        sparkSession = SparkTestHarness.getSparkSession();
        relationClassNameMap = new HashMap<>();
        relationClassNameMap.put("1-1", new TestPerson(new PersonWithOneToOneRelation()));
        relationClassNameMap.put("1-M", new TestPerson(new PersonWithOneToMRelation()));
        relationClassNameMap.put("M-1", new TestPerson(new PersonWithMToOneRelation()));
    }

    @Given("a \"Person\" record that has a {string} relation to a record \"Address\"")
    public void a_person_record_that_has_relation_to_another_record(String multiplicity) {
        this.multiplicity = multiplicity;
        person = relationClassNameMap.get(multiplicity);
    }

    @Given("the \"Address\" records are valid")
    public void the_address_records_are_valid() {
        if (this.multiplicity.equals("1-M")) {
            person.setAddress(createTestAddresses(VALID_ZIPCODE, VALID_STATE));
        } else {
            person.setAddress(createAddressWithSpecified(VALID_ZIPCODE, VALID_STATE));
        }
    }

    @Given("a required \"Address\" record is {string}")
    public void a_address_record_is_invalid(String validity) {
        String INVALID_ZIPCODE = "12345-678910";
        String INVALID_STATE = "NYC";
        if (validity.equals("invalid")) {
            if (this.multiplicity.equals("1-M")) {
                person.setAddress(createTestAddresses(INVALID_ZIPCODE, INVALID_STATE));
            } else {
                person.setAddress(createAddressWithSpecified(INVALID_ZIPCODE, INVALID_STATE));
            }
        }
    }

    @When("validate the \"Person\" record")
    public void the_person_class_is_generated() {
        try {
            person.validate();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("no exception should be thrown")
    public void no_exception_is_thrown() {
        assertNull("There should be no exception is thrown for valid relation data record", exception);
    }

    @Then("the validation exception is thrown")
    public void the_validation_exception_is_thrown() {
        assertNotNull("There should be a validation exception is thrown for invalid relation data record", exception);
    }

    private List<Address> createTestAddresses(String zipCode, String state) {
        return List.of(createAddressWithSpecified(zipCode, state ),
                createAddressWithSpecified(VALID_ZIPCODE, VALID_STATE ));
    }

    private Address createAddressWithSpecified(String zipcode, String state) {
        Address address = new Address();
        address.setStreet(RANDOM_STREETS.get((int)Math.round(Math.random())));
        address.setCity(RANDOM_CITIES.get((int)Math.round(Math.random())));
        address.setZipcode(new Zipcode(zipcode));
        address.setState(new State(state));
        return address;
    }

    public class TestPerson {
        private PersonWithOneToMRelation personWithOneToMRelation;
        private PersonWithMToOneRelation personWithMToOneRelation;
        private PersonWithOneToOneRelation personWithOneToOneRelation;
        public TestPerson(PersonWithOneToMRelation person) {
            this.personWithOneToMRelation = person;
        }

        public TestPerson(PersonWithMToOneRelation person) {
            this.personWithMToOneRelation = person;
        }

        public TestPerson(PersonWithOneToOneRelation person) {
            this.personWithOneToOneRelation = person;
        }

        public void validate() {
            if (this.personWithOneToMRelation != null) {
                this.personWithOneToMRelation.validate();

            } else if (this.personWithMToOneRelation != null) {
                this.personWithMToOneRelation.validate();

            } else {
                this.personWithOneToOneRelation.validate();
            }
        }

        public void setAddress(Address address) {
            if (this.personWithMToOneRelation != null) {
                this.personWithMToOneRelation.setAddress(address);

            } else {
                this.personWithOneToOneRelation.setAddress(address);
            }
        }

        public void setAddress(List<Address> address) {
            this.personWithOneToMRelation.setAddress(address);
        }
    }
}
