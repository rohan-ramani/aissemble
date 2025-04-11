@helmfile-generate
Feature: Helmfile generation migration is performed correctly and only when needed

  Scenario: The project does not have a helmfile. The migration should create it and add itself to the deactivated list
    Given a projects does not have a helm file
    And the pom does not have the migration in the deactivated list
    When the helmfile generation migration is performed
    Then the pom has the migration deactivated
    And the helmfile is generated

  Scenario: The project has a helmfile. The migration should not create it and add itself to the deactivated list
    Given a projects has a helm file
    And the pom does not have the migration in the deactivated list
    When the helmfile generation migration is performed
    Then the helmfile was not changed
