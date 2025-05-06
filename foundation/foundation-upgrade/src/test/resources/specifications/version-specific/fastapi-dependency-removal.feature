@fastapi-dependency-removal
Feature: Remove aissemble-fastapi-chart configuration from values.yaml

  Scenario: Removing aissemble-fastapi-chart configuration from values.yaml
    Given a values.yaml file that has the aissemble-fastapi-chart configuration
    When the 1.13.0 aissemble-fastapi-chart removal migration executes
    Then the aissemble-fastapi-chart configuration is removed from the values.yaml file

  Scenario: Skipping migration when aissemble-fastapi-chart configuration is not present
    Given a values.yaml file that does not have the aissemble-fastapi-chart configuration
    When the 1.13.0 aissemble-fastapi-chart removal migration executes
    Then the aissemble-fastapi-chart removal migration is skipped