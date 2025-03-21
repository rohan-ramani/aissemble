Feature: Remove Data Encryption Dependencies From POM File

  Scenario: Migrate to remove data encryption dependencies from POM file
    Given a POM file that has the data encryption dependencies
    When the 1.13.0 data encryption removal pom migration executes
    Then the data encryption dependencies are removed from the POM file

  Scenario: Skip data encryption migration when there is no data dependencies in the pom file
    Given a POM file that does not have the data encryption dependencies
    When the 1.13.0 data encryption removal pom migration executes
    Then the data encryption removal pom migration is skipped
