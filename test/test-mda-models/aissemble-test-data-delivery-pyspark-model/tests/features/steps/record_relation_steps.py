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
import nose.tools as nt
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


def initialize(context):
    context.relation_class_name_map = {
        "1-1": PersonWithOneToOneRelation,
        "1-M": PersonWithOneToMRelation,
        "M-1": PersonWithMToOneRelation,
    }


@given('a record "Person" that has a "{multiplicity}" relation to a record "Address"')
def step_impl(context, multiplicity):
    initialize(context)
    context.multiplicity = multiplicity


@when('the "Person" class is generated')
def step_impl(context):
    context.person = context.relation_class_name_map.get(context.multiplicity)


@then('"Person" has a method address which returns "{type}"')
def step_impl(context, type):
    address = getattr(context.person, "address")
    return_type = str(signature(address.fget).return_annotation)
    # clean up the return annotation to only show the return type
    return_type = re.sub(
        "(typing\.)|(aissemble_test_data_delivery_pyspark_model\.record\.address\.)|(class)|(<)|(>)|(')|(\s)",
        "",
        str(return_type),
    )
    nt.assert_true(return_type == type)
