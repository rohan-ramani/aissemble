from typing import List

import nose.tools as nt
from behave import given, when, then

from aissemble_test_data_delivery_pyspark_model.dictionary.integer_with_validation import (
    IntegerWithValidation,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.zipcode import Zipcode
from aissemble_test_data_delivery_pyspark_model.record.address import Address
from aissemble_test_data_delivery_pyspark_model.record.city import (
    City,
)
from aissemble_test_data_delivery_pyspark_model.record.mayor import (
    Mayor,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_m_to_one_relation import (
    PersonWithMToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_one_to_one_relation import (
    PersonWithOneToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.state import (
    State,
)
from aissemble_test_data_delivery_pyspark_model.record.street import (
    Street,
)
from aissemble_test_data_delivery_pyspark_model.schema.city_schema import (
    CitySchema,
)
from aissemble_test_data_delivery_pyspark_model.schema.person_with_m_to_one_relation_schema import (
    PersonWithMToOneRelationSchema,
)
from aissemble_test_data_delivery_pyspark_model.schema.person_with_one_to_one_relation_schema import (
    PersonWithOneToOneRelationSchema,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.state_address import (
    StateAddress,
)


@given('the record "City" exists with the following relations')
def step_impl(context):
    # Records and relations are handled in MDA generation
    nt.assert_true(CitySchema() is not None, "City Schema was not generated correctly")


@given('the spark schema is generate for the "City" record')
def step_impl(context):
    context.schema = CitySchema()


@given("a city record is created")
def step_impl(context):
    context.city = _create_city()


@given('a "City" dataSet with a "{validity}" "Mayor" exists')
def step_impl(context, validity):
    city = _create_city()
    if validity == "invalid":
        mayor = Mayor()
        mayor.integer_validation = IntegerWithValidation(0)
        city.mayor = mayor

    context.city_dataset = _create_city_dataframe(context, city)


@given('a "City" dataSet with a "{validity}" "State" exists')
def step_impl(context, validity):
    city = _create_city()
    if validity == "invalid":
        state = State()
        state.integer_validation = IntegerWithValidation(0)
        city.state = state

    context.city_dataset = _create_city_dataframe(context, city)


@given(
    'a "City" dataSet with "{valid_size}" valid "Street" and "{invalid_size}" invalid streets exists'
)
def step_impl(context, valid_size, invalid_size):
    city = _create_city()
    streets: List[Street] = []
    for _ in range(int(valid_size)):
        streets.append(_create_street())

    for _ in range(int(invalid_size)):
        street = _create_street()
        street.integer_validation = IntegerWithValidation(0)
        streets.append(street)

    city.street = streets
    context.city_dataset = _create_city_dataframe(context, city)


@given('the spark schema is generated for the "PersonWithOneToOneRelation" record')
def step_impl(context):
    context.schema = PersonWithOneToOneRelationSchema()


@given('a "{validity}" "PersonWithOneToOneRelation" dataSet exists')
def step_impl(context, validity):
    person_with_one_to_one_relation = PersonWithOneToOneRelation()
    address = _create_address()
    if validity == "invalid":
        address.zipcode = Zipcode("0")
    person_with_one_to_one_relation.address = address
    row = PersonWithOneToOneRelation.as_row(person_with_one_to_one_relation)
    context.person_with_one_to_one_relation = (
        context.test_spark_session.createDataFrame([row], context.schema.struct_type)
    )


@given('the spark schema is generated for the "PersonWithMToOneRelation" record')
def step_impl(context):
    context.schema = PersonWithMToOneRelationSchema()


@given('a "{validity}" "PersonWithMToOneRelation" dataSet exists')
def step_impl(context, validity):
    person_with_many_to_one_relation = PersonWithMToOneRelation()
    address = _create_address()
    if validity == "invalid":
        address.zipcode = Zipcode("0")
    person_with_many_to_one_relation.address = address
    row = PersonWithMToOneRelation.as_row(person_with_many_to_one_relation)
    context.person_with_many_to_one_relation = (
        context.test_spark_session.createDataFrame([row], context.schema.struct_type)
    )


@when('spark schema validation is performed on the "PersonWithMToOneRelation" dataSet')
def step_impl(context):
    person_with_many_to_one_relation_schema = PersonWithMToOneRelationSchema()
    context.validated_dataframe = (
        person_with_many_to_one_relation_schema.validate_dataset(
            context.person_with_many_to_one_relation
        )
    )


@when(
    'spark schema validation is performed on the "PersonWithOneToOneRelation" dataSet'
)
def step_impl(context):
    person_with_one_to_one_relation_schema = PersonWithOneToOneRelationSchema()
    context.validated_dataframe = (
        person_with_one_to_one_relation_schema.validate_dataset(
            context.person_with_one_to_one_relation
        )
    )


@when('a "City" object is mapped to a spark dataset using the record')
def step_impl(context):
    context.city_dataset = _create_city_dataframe(context, context.city)


@when('spark schema validation is performed on the "City" dataSet')
def step_impl(context):
    try:
        context.validated_dataframe = context.schema.validate_dataset(
            context.city_dataset
        )
    except Exception as e:
        context.exc = e


@then('the schema data type for "{record}" is "{type}"')
def step_impl(context, record, type):
    nt.assert_equal(str(context.schema.get_data_type(record.upper())), type)


@then("the dataset has the correct values for the relational objects")
def step_impl(context):
    expected_city = context.city
    for row in context.city_dataset.collect():
        actual_city = City.from_row(row)
        nt.assert_equal(
            actual_city.as_dict(),
            expected_city.as_dict(),
            "The city was not mapped correctly",
        )


@then('the dataSet validation "{success}"')
def step_impl(context, success):
    if success == "passes":
        nt.assert_true(
            context.validated_dataframe is not None,
            "Validation failed when it should have passed",
        )
        nt.assert_true(
            not context.validated_dataframe.isEmpty(),
            "Validation failed when it should have passed",
        )
    else:
        nt.assert_true(
            context.validated_dataframe is None
            or context.validated_dataframe.isEmpty(),
            "Validation passed when it should have failed",
        )


@then("the dataSet validation raises a not implemented error")
def step_impl(context):
    nt.assert_true(context.exc is not None, "No exception was raised when validating")
    nt.assert_true(
        isinstance(context.exc, NotImplementedError),
    )


def _create_city() -> City:
    streets: List[Street] = []
    street = Street()
    street.name = "Street Name"
    street.county = "Street County"
    street.integer_validation = IntegerWithValidation(200)
    streets.append(street)

    mayor = Mayor()
    mayor.integer_validation = IntegerWithValidation(200)
    mayor.name = "Mayor"

    state = State()
    state.integer_validation = IntegerWithValidation(200)
    state.name = "State Name"

    city = City()
    city.street = streets
    city.mayor = mayor
    city.state = state
    return city


def _create_street() -> Street:
    street = Street()
    street.integer_validation = IntegerWithValidation(200)
    street.name = "Street Name"
    street.county = "Street County"
    return street


def _create_city_dataframe(context, city):
    row = City.as_row(city)
    return context.test_spark_session.createDataFrame([row], context.schema.struct_type)


def _create_address() -> Address:
    address = Address()
    address.street = "street address"
    address.city = "city address"
    address.zipcode = Zipcode("12345")
    address.state = StateAddress("ZZ")
    return address
