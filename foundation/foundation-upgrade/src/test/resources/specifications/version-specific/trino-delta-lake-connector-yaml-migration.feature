Feature: Add Delta Lake Connector to Trino Yaml Configuration

  Scenario: Migrate to add delta lake connector config to trino yaml file
    Given a trino yaml file that does not have the delta lake connector configured
    When the trino delta lake connector migration executes
    Then the delta lake connector is configured in the trino yaml file

  Scenario: Skip the migration when thedelta lake connector is configured in the trino yaml file
    Given a trino yaml file with the delta lake connector configured
    When the trino delta lake connector migration executes
    Then the trino delta lake connector migration is skipped
