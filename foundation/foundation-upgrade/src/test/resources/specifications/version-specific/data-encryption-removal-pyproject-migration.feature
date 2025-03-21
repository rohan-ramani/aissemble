Feature: Remove Data Encryption Dependencies From Pyproject File

  Scenario: Migrate to remove data encryption dependencies from Pyproject file
    Given a Pyproject file that has the data encryption dependencies
    When the 1.13.0 data encryption removal pyproject migration executes
    Then the data encryption dependencies are removed from the Pyproject file

  Scenario: Skip data encryption migration when there is no data dependencies in the pom file
    Given a Pyproject file that does not have the data encryption dependencies
    When the 1.13.0 data encryption removal pyproject migration executes
    Then the data encryption removal pyproject migration is skipped
