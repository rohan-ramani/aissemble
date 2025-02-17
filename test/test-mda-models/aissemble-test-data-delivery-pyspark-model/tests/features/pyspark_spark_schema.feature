@pyspark_schema
Feature: Pyspark schema functionality works for records

  This feature captures the cascading validation relation records in the dataset
  If any relation is invalid the base record is invalid
  For the following scenarios, 1-1 and M-1 are treated as equivalent scenarios
  A null entry in the 1-M relation is treated valid

  Background:
    Given the record "City" exists with the following relations
      | multiplicity | record |
      | 1-1          | Mayor  |
      | 1-M          | Street |
      | M-1          | State  |

  Scenario Outline: PySpark schemas generated are able to get the correct data types
    Given the spark schema is generate for the "City" record
    Then the schema data type for "<record>" is "<type>"
    Examples:
      | record | type                                                                                                                                                           |
      | Mayor  | StructType([StructField('name', StringType(), True), StructField('int_v8n', IntegerType(), True)])                                                             |
      | Street | ArrayType(StructType([StructField('name', StringType(), True), StructField('county', StringType(), True), StructField('int_v8n', IntegerType(), True)]), True) |
      | State  | StructType([StructField('name', StringType(), True), StructField('int_v8n', IntegerType(), True)])                                                             |
      | Citizen| ArrayType(StructType([StructField('name', StringType(), True), StructField('int_v8n', IntegerType(), True)]), True)                                                            |

  Scenario: Record generated has working to and from row functionality
    Given the spark schema is generate for the "City" record
    And a city record is created
    When a "City" object is mapped to a spark dataset using the record
    Then the dataset has the correct values for the relational objects

  Scenario Outline: Records with fields with validation rules can be validated using the spark schema
    Given a record with a "<requirement>" field with validation rules
    And the field is set to a "<validity>" value
    And a dataSet containing the record
    And the dataset contains one valid record
    When the generated spark schema validation is performed on the dataSet
    Then the resulting dataSet contains <num> row(s)
    Examples:
      | requirement  | validity | num |
      | required     | valid    | 2   |
      | required     | invalid  | 1   |
      | required     | null     | 1   |
      | non-required | valid    | 2   |
      | non-required | invalid  | 1   |
      | non-required | null     | 2   |

  Scenario: An 1-1 or M-1 relation data record that has invalid data is removed
    Given the following City dataset:
      | Mayor         | Streets      | State       | Citizen                        |
      | valid mayor   | valid street | valid state | valid citizen                  |
      | invalid mayor | valid street | valid state | valid citizen                  |
      | valid mayor   | valid street | valid state | valid citizen, invalid citizen |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |

  Scenario: An 1-M relation data record that has invalid data is removed
    Given the following City dataset:
      | Mayor       | Streets                      | State       | Citizen                        |
      | valid mayor | valid street                 | valid state | valid citizen                  |
      | valid mayor | invalid street, valid street | valid state | valid citizen                  |
      | valid mayor | valid street, valid street   | valid state | valid citizen, invalid citizen |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |

  Scenario: A required 1-1 or M-1 relation data record that is not set is removed
    Given the following City dataset:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | null        | valid street | valid state | valid citizen |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |

  Scenario: A non-required 1-1 or M-1 relation data record that is not set is preserved
    Given the following City dataset:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid mayor | valid street | null        | valid citizen |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid mayor | valid street | null        | valid citizen |

  Scenario: A required 1-M relation data record that is empty is removed
    Given the following City dataset:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid mayor | valid street | valid state | []            |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |

  Scenario: A required 1-M relation data record that is not set is removed
    Given the following City dataset:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid mayor | valid street | valid state | null          |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |

  Scenario: A non-required 1-M relation data record that is not set is preserved
    Given the following City dataset:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid       | null         | valid state | valid citizen |
    When the dataset is validated against the schema
    Then the result dataset should match:
      | Mayor       | Streets      | State       | Citizen       |
      | valid mayor | valid street | valid state | valid citizen |
      | valid mayor | null         | valid state | valid citizen |

