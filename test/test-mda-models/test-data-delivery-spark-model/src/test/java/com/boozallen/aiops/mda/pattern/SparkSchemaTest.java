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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.boozallen.aiops.mda.pattern.dictionary.Zipcode;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToMRelationSchema;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.boozallen.aiops.mda.pattern.dictionary.IntegerWithValidation;
import com.boozallen.aiops.mda.pattern.dictionary.StringWithValidation;
import com.boozallen.aiops.mda.pattern.record.Address;
import com.boozallen.aiops.mda.pattern.record.City;
import com.boozallen.aiops.mda.pattern.record.CitySchema;
import com.boozallen.aiops.mda.pattern.record.Mayor;
import com.boozallen.aiops.mda.pattern.record.PersonWithMToOneRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithMToOneRelationSchema;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToOneRelation;
import com.boozallen.aiops.mda.pattern.record.PersonWithOneToOneRelationSchema;
import com.boozallen.aiops.mda.pattern.record.RecordWithNonRequiredValidation;
import com.boozallen.aiops.mda.pattern.record.RecordWithNonRequiredValidationSchema;
import com.boozallen.aiops.mda.pattern.record.RecordWithRequiredValidation;
import com.boozallen.aiops.mda.pattern.record.RecordWithRequiredValidationSchema;
import com.boozallen.aiops.mda.pattern.record.State;
import com.boozallen.aiops.mda.pattern.record.Street;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkSchemaTest {
    String recordWithValidatedFieldRequirement;
    CitySchema citySchema;
    PersonWithOneToOneRelationSchema personWithOneToOneRelationSchema;
    PersonWithMToOneRelationSchema personWithMToOneRelationSchema;
    PersonWithOneToMRelationSchema personWithOneToMRelationSchema;
    RecordWithRequiredValidationSchema recordWithRequiredValidationSchema;
    RecordWithNonRequiredValidationSchema recordWithNonRequiredValidationSchema;
    RecordWithNonRequiredValidation recordWithNonRequiredValidation;
    RecordWithRequiredValidation recordWithRequiredValidation;
    List<Row> recordWithRequirementValidationRows;
    SparkSession spark;
    Dataset<Row> cityDataSet;
    Dataset<Row> personWithOneToOneRelationDataSet;
    Dataset<Row> personWithMToOneRelationDataSet;
    Dataset<Row> personWithOneToMRelationDataSet;
    Dataset<Row> recordWithValidatedFieldDataSet;
    Dataset<Row> validatedDataSet;
    Exception exception;

    @Before("@SparkSchema")
    public void setUp() {
        this.spark = SparkTestHarness.getSparkSession();
        this.recordWithRequirementValidationRows = new ArrayList<>();
    }

    @Given("the record \"City\" exists with the following relations")
    public void theRecordExistsWithTheFollowingRelations(Map<String, String> multiplicity) {
        // Handled with MDA generation
    }

    @Given("the spark schema is generated for the \"PersonWithOneToOneRelation\" record")
    public void theSparkSchemaIsGeneratedForThePersonWithOneToOneRelationRecord() {
        this.personWithOneToOneRelationSchema = new PersonWithOneToOneRelationSchema();
    }

    @Given("a valid \"City\" dataSet exists")
    public void aValidDataSetExists() {
        List<Row> rows = Collections.singletonList(CitySchema.asRow(createCity()));
        this.cityDataSet = spark.createDataFrame(rows, this.citySchema.getStructType());
    }

    @Given("a {string} \"PersonWithOneToOneRelation\" dataSet exists")
    public void aValidPersonWithOneToOneRelationDataSetExists(String validity) {
        PersonWithOneToOneRelation personWithOneToOneRelation = new PersonWithOneToOneRelation();
        if (StringUtils.equals("valid", validity)){
            personWithOneToOneRelation.setAddress(createAddress());
        } else {
            Address address = createAddress();
            address.setStreet(null);
            personWithOneToOneRelation.setAddress(address);
        }

        List<Row> rows = Collections.singletonList(PersonWithOneToOneRelationSchema.asRow(personWithOneToOneRelation));
        this.personWithOneToOneRelationDataSet = spark.createDataFrame(rows,
                this.personWithOneToOneRelationSchema.getStructType());
    }

    @Given("the spark schema is generated for the \"PersonWithMToOneRelation\" record")
    public void theSparkSchemaIsGeneratedForThePersonWithMToOneRelationRecord() {
        this.personWithMToOneRelationSchema = new PersonWithMToOneRelationSchema();
    }

    @Given("a {string} \"PersonWithMToOneRelation\" dataSet exists")
    public void aValidPersonWithManyToOneRelationDataSetExists(String validity) {
        PersonWithMToOneRelation personWithOneToOneRelation = new PersonWithMToOneRelation();
        if (StringUtils.equals("valid", validity)){
            personWithOneToOneRelation.setAddress(createAddress());
        } else {
            Address address = createAddress();
            address.setStreet(null);
            personWithOneToOneRelation.setAddress(address);
        }

        List<Row> rows = Collections.singletonList(PersonWithMToOneRelationSchema.asRow(personWithOneToOneRelation));
        this.personWithMToOneRelationDataSet = spark.createDataFrame(rows,
                this.personWithMToOneRelationSchema.getStructType());
    }

    @Given("a \"City\" dataSet with an invalid relation exists")
    public void aCityDataSetWithAnInvalidRelationExists() {
        IntegerWithValidation integerWithValidation = new IntegerWithValidation(0);
        Mayor mayor = new Mayor();
        mayor.setName("Sam");
        mayor.setIntegerValidation(integerWithValidation);

        City city = createCity();
        city.setMayor(mayor);
        List<Row> rows = Collections.singletonList(CitySchema.asRow(city));
        this.cityDataSet = spark.createDataFrame(rows, this.citySchema.getStructType());
    }

    @Given("a record with a {string} field with validation rules")
    public void aRecordWithAFieldWithValidationRules(String requirement) {
        this.recordWithValidatedFieldRequirement = requirement;

        if (requirement.equals("required")) {
            this.recordWithRequiredValidation = new RecordWithRequiredValidation();
        } else {
            this.recordWithNonRequiredValidation = new RecordWithNonRequiredValidation();
        }
    }

    @Given("the field is set to a {string} value")
    public void theFieldIsSetToAValue(String validity) {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            // set valid fields to verify validation still works with multiple fields
            this.recordWithRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            this.recordWithRequiredValidation.setStringSimple("Test123");

            if (validity.equals("valid")) {
                this.recordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            } else if(validity.equals("invalid")) {
                this.recordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(50));
            } else {
                // Do nothing to keep the field null
            }
        } else {
            // set valid fields to verify validation still works with multiple fields
            this.recordWithNonRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            this.recordWithNonRequiredValidation.setStringSimple("Test123");
            
            if (validity.equals("valid")) {
                this.recordWithNonRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            } else if(validity.equals("invalid")) {
                this.recordWithNonRequiredValidation.setIntegerValidation(new IntegerWithValidation(50));
            } else {
                // Do nothing to keep the field null
            }
        }
    }

    @Given("a dataSet containing the record")
    public void aDataSetContainingTheRecord() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            this.recordWithRequirementValidationRows.add(RecordWithRequiredValidationSchema.asRow(this.recordWithRequiredValidation));
        } else {
            this.recordWithRequirementValidationRows.add(RecordWithNonRequiredValidationSchema.asRow(this.recordWithNonRequiredValidation));
        }
    }

    @Given("the dataset contains one valid record")
    public void theDataSetContainsOneValidRecord() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            RecordWithRequiredValidation validRecordWithRequiredValidation = new RecordWithRequiredValidation();
            validRecordWithRequiredValidation.setIntegerValidation(new IntegerWithValidation(150));
            validRecordWithRequiredValidation.setStringValidation(new StringWithValidation("Test123"));
            validRecordWithRequiredValidation.setStringSimple("Test123");

            this.recordWithRequirementValidationRows.add(RecordWithRequiredValidationSchema.asRow(validRecordWithRequiredValidation));
        } else {
            RecordWithNonRequiredValidation validRecordWithNonRequiredValidation = new RecordWithNonRequiredValidation();
            
            this.recordWithRequirementValidationRows.add(RecordWithNonRequiredValidationSchema.asRow(validRecordWithNonRequiredValidation));
        }
    }

    @When("the spark schema is generated for the \"City\" record")
    public void theSparkSchemaIsGeneratedForTheCityRecord() {
        this.citySchema = new CitySchema();
    }

    @When("a \"City\" POJO is mapped to a spark dataset using the schema")
    public void aSparkDatasetExists() {
        City expectedCity = createCity();
        List<Row> cityRows = Collections.singletonList(CitySchema.asRow(expectedCity));

        this.cityDataSet = this.spark.createDataFrame(cityRows, this.citySchema.getStructType());
    }

    @When("spark schema validation is performed on the \"PersonWithOneToOneRelation\" dataSet")
    public void sparkSchemaValidationIsPerformedOnThePersonWithOneToOneRelationDataSet() {
        try {
            this.validatedDataSet = this.personWithOneToOneRelationSchema.validateDataFrame(this.personWithOneToOneRelationDataSet);
        }catch (Exception e) {
            this.exception = e;
        }
    }

    @When("spark schema validation is performed on the \"PersonWithMToOneRelation\" dataSet")
    public void sparkSchemaValidationIsPerformedOnThePersonWithMToOneRelationDataSet() {
        try {
            this.validatedDataSet =
                    this.personWithMToOneRelationSchema.validateDataFrame(this.personWithMToOneRelationDataSet);
        }catch (Exception e) {
            this.exception = e;
        }
    }

    @When("spark schema validation is performed on the \"City\" dataSet")
    public void sparkSchemaValidationIsPerformedOnTheCityDataSet() {
        try {
            this.validatedDataSet = this.citySchema.validateDataFrame(this.cityDataSet);
        }catch (Exception e) {
            this.exception = e;
        }
    }

    @When("the generated spark schema validation is performed on the dataSet")
    public void theGeneratedSparkSchemaValidationIsPerformedOnTheDataSet() {
        if (this.recordWithValidatedFieldRequirement.equals("required")) {
            this.recordWithRequiredValidationSchema = new RecordWithRequiredValidationSchema();

            this.recordWithValidatedFieldDataSet = this.spark.createDataFrame(
                this.recordWithRequirementValidationRows,
                this.recordWithRequiredValidationSchema.getStructType()
            );

            this.validatedDataSet = this.recordWithRequiredValidationSchema.validateDataFrame(this.recordWithValidatedFieldDataSet);
        } else {
            this.recordWithNonRequiredValidationSchema = new RecordWithNonRequiredValidationSchema();
            
            this.recordWithValidatedFieldDataSet = this.spark.createDataFrame(
                this.recordWithRequirementValidationRows,
                this.recordWithNonRequiredValidationSchema.getStructType()
            );

            this.validatedDataSet = this.recordWithNonRequiredValidationSchema.validateDataFrame(this.recordWithValidatedFieldDataSet);
        }
    }

    @Then("the validation fails with NotYetImplementedException")
    public void theValidationFailsWithNotYetImplementedException() {
        assertNotNull("No exception was thrown", this.exception);
        assertNotNull("Throw exception is not of instance NotImplementedException", this.exception instanceof
                NotImplementedException ? (this.exception) : null);
    }

    @Then("the schema data type for {string} is {string}")
    public void theSchemaDataTypeForIs(String record, String type) {
        assertEquals("The type for record is not correct", type,
                this.citySchema.getDataType(record.toUpperCase()).toString());
    }

    @Then("the dataset has the correct values for the relational objects")
    public void aPOJOCanBeMappedToASparkRow() {
        City expectedCity = createCity();
        for (Row row : this.cityDataSet.collectAsList()) {
            City actualCity = CitySchema.mapRow(row);
            assertEquals("City did not map correctly. Incorrect number of Street relations",
                    expectedCity.getStreet().size(), actualCity.getStreet().size());
            assertEquals("City did not map correctly. Incorrect Street relation",
                    expectedCity.getStreet().get(0).toJson(), actualCity.getStreet().get(0).toJson());
            assertEquals("City did not map correctly. Incorrect Mayor relation", expectedCity.getMayor().toJson(),
                    actualCity.getMayor().toJson());
            assertEquals("City did not map correctly. Incorrect State relation", expectedCity.getState().toJson(),
                    actualCity.getState().toJson());
        }
    }

    @Then("the dataSet validation {string}")
    public void theDataSetValidationIsSuccessful(String succeed) {
        if(StringUtils.equals("fails", succeed)) {
            assertTrue("Validation passed when it should have failed", validatedDataSet.isEmpty());
        } else {
            assertNotNull("Validation failed when it should have passed", validatedDataSet);
            assertFalse("Validation failed when it should have passed", validatedDataSet.isEmpty());
        }
    }

    @Then("the resulting dataSet contains {int} row\\(s)")
    public void theResultingDataSetContainsRows(int numRows) {
        assertEquals("The validated dataSet contained the incorrect number of rows", numRows, this.validatedDataSet.count());
    }

    private City createCity(){
        IntegerWithValidation integerWithValidation = new IntegerWithValidation(100);

        List<Street> streets = new ArrayList<>();
        Street street = new Street();
        street.setName("Street 1 Name");
        street.setCounty("County 2 Name");
        street.setIntegerValidation(integerWithValidation);
        streets.add(street);
        Street street2 = new Street();
        street2.setName("Street 2 Name");
        street2.setCounty("County 2 Name");
        street2.setIntegerValidation(integerWithValidation);
        streets.add(street2);

        State state = new State();
        state.setName("Maryland");

        Mayor mayor = new Mayor();
        mayor.setName("Sam");
        mayor.setIntegerValidation(integerWithValidation);

        City city = new City();
        city.setStreet(streets);
        city.setMayor(mayor);
        city.setState(state);
        return city;
    }

    private Address createAddress(){
        Address address = new Address();
        address.setZipcode(new Zipcode("12345-1234"));
        address.setCity("City");
        address.setState(new com.boozallen.aiops.mda.pattern.dictionary.State("CA"));
        address.setStreet("Street");
        return address;
    }
}
