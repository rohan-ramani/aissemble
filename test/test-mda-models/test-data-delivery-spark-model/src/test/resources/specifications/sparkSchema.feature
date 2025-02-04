@SparkSchema
Feature: Records with relations are generated correctly and function as expected

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

  Scenario: Spark schemas generated fails to validate with not yet implemented exception
    Given the spark schema is generated for the "City" record
    And a valid "City" dataSet exists
    When spark schema validation is performed on the dataSet
    Then the dataSet validation "passes"

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

  Scenario Outline: Records with a One to Many relation can be validated using the spark schema
    Given the spark schema is generated for the "PersonWithOneToMRelation" record
    And a "<validity>" "PersonWithOneToMRelation" dataSet exists
    When spark schema validation is performed on the "PersonWithOneToMRelation" dataSet
    Then the dataSet validation "<success>"
    Examples:
      | validity | success |
      | valid    | passes  |
      | invalid  | fails   |
