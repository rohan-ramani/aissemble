@enforce-docker-executable
Feature: Validate if docker is executable and running

  Scenario: Docker host environment variable is present and reachable and set to the current docker context, enforcer is successful
    Given the docker_host environment variable is "present"
    And the docker context is set to default
    And the location is "reachable"
    When the docker executable enforcer is called
    Then the enforcer passes successfully

  Scenario: Docker host environment variable is present and not reachable, an error is thrown
    Given the docker_host environment variable is "present"
    And the location is "unreachable"
    When the docker executable enforcer is called
    Then an error message is throw with ".*Cannot connect to the Docker daemon at the DOCKER_HOST environment variable location .*. Is the docker daemon running?"

  Scenario: Docker host environment variable is not present but the default location is present, enforcer is successful
    Given the docker_host environment variable is "absent"
    And the docker context value is "rancher-desktop"
    And the default location is "reachable"
    When the docker executable enforcer is called
    Then the enforcer passes successfully

  Scenario: Docker host environment variable is not present and the Docker context is default but not present, an error is thrown.
    Given the docker_host environment variable is "absent"
    And the docker context is set to default
    And the location is "unreachable"
    When the docker executable enforcer is called
    Then an error message is throw with ".*Cannot connect to the Docker daemon at .*. Is the docker daemon running?"

  Scenario: Docker host environment variable and the default location is not present and the Docker context is not default, an error is thrown.
    Given the docker_host environment variable is "absent"
    And the docker context value is "rancher-desktop"
    And the default location is "unreachable"
    When the docker executable enforcer is called
    Then an error message is throw with ".*Environment variable DOCKER_HOST needs to be set to the current Docker context host url.*"

  Scenario: The operating system is Windows then a warning is displayed stating the enforcer can not be ran
  and to make sure docker_host is set
    Given the detected operating system is "windows"
    When the docker executable enforcer is called
    Then a warning is logged telling users the enforcer is skipped and to set DOCKER_HOST
    And the enforcer passes successfully

  Scenario: The docker context url schema is not unix then the enforcer will be skip
    Given the docker context path does not have a unix schema
    When the docker executable enforcer is called
    Then a warning is logged telling users the enforcer is skipped and to set DOCKER_HOST
    And the enforcer passes successfully

  Scenario: The docker enforcer rule is disabled, the rule will complete successfully
    Given the docker enforcer rule is disabled
    When the docker executable enforcer is called
    And the enforcer passes successfully