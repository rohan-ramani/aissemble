###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
import re
import random
import nose.tools as nt
from typing import List
from behave import given, when, then  # pylint: disable=no-name-in-module
from inspect import signature
from aissemble_test_data_delivery_pyspark_model.record.person_with_m_to_one_relation import (
    PersonWithMToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_one_to_one_relation import (
    PersonWithOneToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_one_to_m_relation import (
    PersonWithOneToMRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.address import (
    Address,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.zipcode import (
    Zipcode,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.state import (
    State,
)

RANDOM_STREETS = ["123 ABC St.", "1st Street"]
RANDOM_CITIES = ["New York", "New York"]
VALID_ZIPCODE = "12345-6789"
VALID_STATE = "NY"


def initialize(context):
    context.relation_class_map = {
        "1-1": PersonWithOneToOneRelation(),
        "1-M": PersonWithOneToMRelation(),
        "M-1": PersonWithMToOneRelation(),
    }


@given('a "Person" record that has a "{multiplicity}" relation to a record "Address"')
def step_impl(context, multiplicity):
    initialize(context)
    context.multiplicity = multiplicity
    context.person = context.relation_class_map.get(multiplicity)


@given('the "Address" records are valid')
def step_impl(context):
    if context.multiplicity == "1-M":
        context.person.address = create_test_addresses(VALID_ZIPCODE, VALID_STATE)
    else:
        context.person.address = create_address_with_specified(
            VALID_ZIPCODE, VALID_STATE
        )


@given('a required "Address" record is "{validity}"')
def step_impl(context, validity):
    INVALID_ZIPCODE = "12345-678910"
    INVALID_STATE = "NYC"
    if validity == "invalid":
        if context.multiplicity == "1-M":
            context.person.address = create_test_addresses(
                INVALID_ZIPCODE, INVALID_STATE
            )
        else:
            context.person.address = create_address_with_specified(
                INVALID_ZIPCODE, INVALID_STATE
            )


@when('validate the "Person" record')
def step_impl(context):
    context.validation_exception = None
    try:
        context.person.validate()
    except ValueError as err:
        context.validation_exception = err


@then("no Exception should be thrown")
def step_impl(context):
    nt.assert_true(context.validation_exception is None)


@then("the validation exception is thrown")
def step_impl(context):
    nt.assert_true(context.validation_exception is not None)


def create_test_addresses(zipcode: str, state: str) -> List[Address]:
    return [
        create_address_with_specified(zipcode, state),
        create_address_with_specified(VALID_ZIPCODE, VALID_STATE),
    ]


def create_address_with_specified(zipcode: str, state: str) -> Address:
    address = Address()
    address.street = RANDOM_STREETS[random.randint(0, 1)]
    address.city = RANDOM_CITIES[random.randint(0, 1)]
    address.zipcode = Zipcode(zipcode)
    address.state = State(state)
    return address
