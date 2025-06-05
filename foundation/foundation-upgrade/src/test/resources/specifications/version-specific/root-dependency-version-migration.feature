@dependency-version-migration
Feature: aiSSEMBLE dependency version migration

  Scenario: A project that has aissemble dependencies with hard coded version are migrated to use maven properties
    Given a project with hardcoded aissemble versions in the root pom
    When the dependency version migration is executed
    Then the aissemble versions are update to use the maven property

  Scenario: A project that has aissemble dependencies that are use the maven property are not migrated
    Given a project with aissemble versions using the maven property in the root pom
    When the dependency version migration is executed
    Then the dependency version migration is skipped