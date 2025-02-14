@SparkSchema
Feature: Record spark schemas are generated correctly and function as expected

  Background:
    Given the record "City" exists with the following relations
      | multiplicity | record |
      | 1-1          | Mayor  |
      | 1-M          | Street |
      | M-1          | State  |

  Scenario Outline: Spark schemas generated are able to get the correct data types
    When the spark schema is generated for the "City" record
    Then the schema data type for "<record>" is "<type>"
    Examples:
      | record | type                                                                                                                                    |
      | Mayor  | StructType(StructField(name,StringType,true),StructField(int_v8n,IntegerType,true))                                                     |
      | Street | ArrayType(StructType(StructField(name,StringType,true),StructField(county,StringType,true),StructField(int_v8n,IntegerType,true)),true) |
      | State  | StructType(StructField(name,StringType,true))                                                                                           |

  Scenario: Spark schemas generated has working to and from POJO functionality
    Given the spark schema is generated for the "City" record
    When a "City" POJO is mapped to a spark dataset using the schema
    Then the dataset has the correct values for the relational objects

  # TODO validation for invalid relations should fail
  Scenario Outline: Records with a One to One relation can be validated using the spark schema
    Given the spark schema is generated for the "PersonWithOneToOneRelation" record
    And a "<validity>" "PersonWithOneToOneRelation" dataSet exists
    When spark schema validation is performed on the "PersonWithOneToOneRelation" dataSet
    Then the dataSet validation "<success>"
    Examples:
      | validity | success |
      | valid    | passes  |
      | invalid  | passes  |

  # TODO validation for invalid relations should fail
  Scenario Outline: Records with a Many to One relation can be validated using the spark schema
    Given the spark schema is generated for the "PersonWithMToOneRelation" record
    And a "<validity>" "PersonWithMToOneRelation" dataSet exists
    When spark schema validation is performed on the "PersonWithMToOneRelation" dataSet
    Then the dataSet validation "<success>"
    Examples:
      | validity | success |
      | valid    | passes  |
      | invalid  | passes  |

  # TODO validation for One to Many relations should include pass/fail testing
  Scenario: Spark schemas generated validates One to Many relations
    Given the spark schema is generated for the "City" record
    And a valid "City" dataSet exists
    When spark schema validation is performed on the "City" dataSet
    Then the dataSet validation "passes"

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
