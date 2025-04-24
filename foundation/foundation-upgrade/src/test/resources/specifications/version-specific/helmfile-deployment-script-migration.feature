@helmfile-deployment-script
Feature: The helmfile deployment script migration updated the existing deployment script to use helmfile

  Scenario: The project has an old deployment script. The migration should update it to use helmfile
    Given a projects has a deployment script
    And the helmfile deployment migration is set to run
    When the helmfile deployment script migration is performed
    Then the deploy file is updated

  Scenario: The project has an old jenkinsPipelineSteps. The migration should execute and delete it
    Given a projects has a jenkins pipeline steps file
    And the helmfile deployment migration is set to run
    When the helmfile deployment script migration is performed
    Then the jenkins Pipeline steps file is deleted