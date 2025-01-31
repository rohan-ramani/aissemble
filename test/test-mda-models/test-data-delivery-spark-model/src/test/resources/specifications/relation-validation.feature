@relation-validation
Feature: Validate record with relation configuration
Scenario Outline: Record with valid relation data
  Given a "Person" record that has a "<multiplicity>" relation to a record "Address"
  And the "Address" records are valid
  When validate the "Person" record
  Then no exception should be thrown
 Examples:
  | multiplicity |
  | 1-1          |
  | 1-M          |
  | M-1          |

Scenario Outline: Record with invalid relation data
  Given a "Person" record that has a "<multiplicity>" relation to a record "Address"
  And a required "Address" record is "<validity>"
  When validate the "Person" record
  Then the validation exception is thrown
 Examples:
  | multiplicity | validity     |
  | 1-1          | missing      |
  | 1-1          | invalid      |
  | 1-M          | missing      |
  | 1-M          | invalid      |
  | M-1          | missing      |
  | M-1          | invalid      |