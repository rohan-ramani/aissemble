Feature: Inference Docker Image Tag Migration

  Scenario Outline: Migrate the inference docker image tag when there is no image.tag or image.imagePullPolicy configuration defined
      Given an inference dev YAML file with no "<image configuration>" defined
      When the 1.11.0 inference docker image tag migration executes
      Then the image configuration is added

      Examples:
          | image configuration    |
          | image                  |
          | image-tag              |
          | image-pull-policy      |
          | image-tag-pull-policy  |




  Scenario: Skip a inference docker image tag migration when there is expected image configuration
    Given an inference dev YAML file with expected image configuration defined
    When the 1.11.0 inference docker image tag migration executes
    Then inference docker image migration is skipped