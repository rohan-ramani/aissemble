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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Test record generation for relation.feature
 */
public class RelationTest {

    private final String packageName = "com.boozallen.aiops.mda.pattern.record";
    private Map<String, String> relationClassNameMap;
    private String className;
    private Class<?> personClass;

    @Before("@relation")
    public void setupRelationClassMap() {
        relationClassNameMap = new HashMap<>();
        relationClassNameMap.put("1-1", "PersonWithOneToOneRelation");
        relationClassNameMap.put("1-M", "PersonWithOneToMRelation");
        relationClassNameMap.put("M-1", "PersonWithMToMRelation");
    }

    @Given("a record \"Person\" that has a {string} relation to a record \"Address\"")
    public void a_record_person_that_has_relation_to_another_record(String multiplicity) {
        className = relationClassNameMap.get(multiplicity);

    }

    @When("the \"Person\" class is generated")
    public void the_person_class_is_generated() throws ClassNotFoundException {
        personClass = Class.forName(packageName + "." + className);
    }

    @Then("\"Person\" has a method getAddress which returns {string}")
    public void person_has_a_method_get_address_which_returns(String type) throws NoSuchMethodException {
        Method getAddress = personClass.getMethod("getAddress", null);
        Class<?> returnType = getAddress.getReturnType();
        switch (type) {
            case "Address" -> assertClass(packageName, type, returnType);
            case "List<Address>" -> {
                String[] classNames = type.split("([<>])");
                assertClass("java.util", classNames[0], returnType);
                Type contentType = getAddress.getGenericReturnType();
                if (contentType instanceof ParameterizedType elementType) {
                    Type element = elementType.getActualTypeArguments()[0];
                    assertClass(packageName, classNames[1], (Class<?>) element);
                }
            }
        }
    }

    private void assertClass(String classPackageName, String simpleClassName, Class<?> clazz) {
        assertEquals(simpleClassName,  clazz.getSimpleName());
        assertEquals(classPackageName, clazz.getPackageName());
    }
}
