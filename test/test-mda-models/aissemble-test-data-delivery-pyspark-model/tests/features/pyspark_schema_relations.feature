@pyspark_schema_relation
Feature: Pyspark schema functionality works for relations

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

  Scenario: Record generated has working to and from row functionality
    Given the spark schema is generate for the "City" record
    And a city record is created
    When a "City" object is mapped to a spark dataset using the record
    Then the dataset has the correct values for the relational objects

  Scenario Outline: Records with a One to One relation can be validated using the spark schema
    Given the spark schema is generated for the "PersonWithOneToOneRelation" record
    And a "<validity>" "PersonWithOneToOneRelation" dataSet exists
    When spark schema validation is performed on the "PersonWithOneToOneRelation" dataSet
    Then the dataSet validation "<success>"
    Examples:
      | validity | success |
      | valid    | passes  |
      | invalid  | fails   |

  Scenario Outline: Records with a Many to One relation can be validated using the spark schema
    Given the spark schema is generated for the "PersonWithMToOneRelation" record
    And a "<validity>" "PersonWithMToOneRelation" dataSet exists
    When spark schema validation is performed on the "PersonWithMToOneRelation" dataSet
    Then the dataSet validation "<success>"
    Examples:
      | validity | success |
      | valid    | passes  |
      | invalid  | fails   |

  Scenario Outline: Spark schemas generated fails to validate One to Many relations with not yet implemented exception
    Given the spark schema is generate for the "City" record
    And a "City" dataSet with "<valid_size>" valid "Street" and "<invalid_size>" invalid streets exists
    When spark schema validation is performed on the "City" dataSet
    Then the dataSet validation raises a not implemented error
    Examples:
      | valid_size | invalid_size |
      | 1          | 0            |
      | 0          | 1            |
      | 1          | 1            |

