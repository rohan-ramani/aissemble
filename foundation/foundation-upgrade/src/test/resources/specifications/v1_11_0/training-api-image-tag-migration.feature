Feature: Training API Image Tag Migration

  Scenario: Migrate the training api image tag when there is a latest image tag configuration
    Given a training api yaml has latest image tag configuration under trainingApiConfigDev
    When the 1.11.0 training api image tag migration executes
    Then the image tag configuration is removed

  Scenario: Skip a training api image tag migration when there is no latest image tag configuration
    Given a training api yaml has no image tag configuration under trainingApiConfigDev
    When the 1.11.0 training api image tag migration executes
    Then training api image tag migration is skipped