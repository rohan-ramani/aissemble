@helmfile
Feature: Helmfile migration is performed correctly and only when needed

  Scenario: The aissemble version is updated when it does not match the current version
    Given a projects helmfile values has an out of date aissemble version
    When the helmfile version migration is performed
    Then the helmfile value is update

  Scenario: The aissemble version is not updated when it already matches the current version
    Given a projects helmfile values has the desired aissemble version
    When the helmfile version migration is performed
    Then the migration is not performed
