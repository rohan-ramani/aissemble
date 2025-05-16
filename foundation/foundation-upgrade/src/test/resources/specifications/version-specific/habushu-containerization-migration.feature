Feature: Habushu Containerization Migration

  Migrations to prepare projects to leverage Habushu's containerize-dependencies goal to package
  their python pipelines

  Scenario: Habushu modules with dependencies on other Habushu modules use the correct type
    Given a POM with packaging habushu
    And a pom-type dependency on another habushu-packaged module that is in the list of Maven projects
    When the Habushu dependency type migration executes
    Then the type of the monorepo dependency is updated to habushu
