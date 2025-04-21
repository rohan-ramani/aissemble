Feature: Migrate MySql Connector Java dependency

  Scenario: spark-application chart value files are migrated
    Given a spark-application values file that references mysql-connector-java in sparkApp.spec.deps.jars
    When the MySqlConnector yaml migration executes
    Then the mysql-connector-java jar coordinates are updated to mysql-connector-j jar

  Scenario: SparkApplication files are migrated
    Given a spark-application values file that already references mysql-connector-j in spec.deps.jars
    When the MySqlConnector yaml migration executes
    Then the MySqlConnector migration is skipped
