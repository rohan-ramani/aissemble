Feature: Remove Hive, Spark, and Hadoop from the Spark Pipeline Shaded Jar Dependencies Migration

  Scenario Outline: Migrate the pom files to exclude the hive, spark and hadoop dependencies from shaded jar
    Given a POM contains extensions-data-delivery-spark dependency and "<includes>" hive, spark, hadoop dependencies
    When the 1.13.0 spark pipeline jar dependencies migration executes
    Then the pom file is updated

    Examples:
      | includes              |
      | no                   |
      | some                 |

  Scenario: Skip migration with the pom which has no extensions-data-delivery-spark dependency file
    Given a POM has no extensions-data-delivery-spark dependency
    When the 1.13.0 spark pipeline jar dependencies migration executes
    Then the spark pipeline jar dependencies migration is skipped

  Scenario: Skip migration with the pom which has extensions-data-delivery-spark, hive, spark, hadoop dependencies
    Given a POM with extensions-data-delivery-spark, and hive, spark, hadoop dependencies defined correctly
    When the 1.13.0 spark pipeline jar dependencies migration executes
    Then the spark pipeline jar dependencies migration is skipped