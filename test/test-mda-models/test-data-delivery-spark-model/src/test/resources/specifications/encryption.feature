@encryption
Feature: Step encryption are generated correctly and function as expected
  
  Scenario: The policies location property can be undefined for encryption policies
    Given a pipeline with an inbound data type
    And the policies location property is not defined
    When the check and apply encryption method is called
    Then the method completes without applying encryption