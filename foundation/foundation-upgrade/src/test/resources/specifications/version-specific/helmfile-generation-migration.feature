@helmfile-generate
Feature: Helmfile generation migration is performed correctly and only when needed

  Scenario: The project does not have helmfiles. The migration should create them when the activation key is set
    Given a projects does not have helm files
    And the system property `aissemble.enable.helmfile.migration` is set
    When the helmfile generation migration is performed
    Then the helmfiles are generated

  Scenario: The project has a helmfile. The migration should not create it and when the activation key is set
    Given a projects has helm files
    And the system property `aissemble.enable.helmfile.migration` is set
    When the helmfile generation migration is performed
    Then the helmfiles were not changed

  Scenario: The migration should not run when the activation key is not set
    Given the system property `aissemble.enable.helmfile.migration` is not set
    When the helmfile generation migration is performed
    Then the helmfile generation is skipped
