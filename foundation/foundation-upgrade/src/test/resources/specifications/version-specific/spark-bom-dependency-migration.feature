Feature: Add aissemble-spark-bom dependency to POM file

  Scenario: Adding aissemble-spark-bom dependency to POM file
    Given a POM file that doesn't have the aissemble-spark-bom dependency
    And contains a spark dependency
    And includes aissemble build-parent in its hierarchy
    When the aissemble-spark-bom pom migration executes
    Then the aissemble-spark-bom dependency is added to the POM file

  Scenario: Skip migration if spark dependency is not present
    Given a POM file that doesn't have the aissemble-spark-bom dependency with no spark dependency
    And includes aissemble build-parent in its hierarchy
    But doesn't contain a spark dependency
    When the aissemble-spark-bom pom migration executes
    Then the migration is skipped

  Scenario: Adding aissemble-spark-bom dependency to already existing dependencyManagement
    Given a POM file that doesn't have the aissemble-spark-bom dependency with an existing dependencyManagement section
    And includes aissemble build-parent in its hierarchy
    And contains a spark dependency
    When the aissemble-spark-bom pom migration executes
    Then the aissemble-spark-bom dependency is added to the POM file

