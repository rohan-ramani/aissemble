@habushu-containerize
Feature: Habushu Containerization Migration

  Migrations to prepare projects to leverage Habushu's containerize-dependencies goal to package
  their python pipelines

  Scenario: Habushu modules with dependencies on other Habushu modules use the correct type
    Given a POM with packaging habushu
    And a pom-type dependency on another habushu-packaged module that is in the list of Maven projects
    When the Habushu dependency type migration executes
    Then the type of the monorepo dependency is updated to habushu

  Scenario: ML Training docker pom files are updated to leverage the Habushu containerize goal
    Given an training POM without a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-training-docker
    When the training docker pom migration executes
    Then the POM is updated to use the habushu containerize goal

  Scenario: ML Training docker POM file has Habushu containerize goal, the migration should be skipped
    Given an training POM with a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-training-docker
    When the training docker pom migration executes
    Then the training docker pom migration was skipped