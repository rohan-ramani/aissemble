@relation
Feature: Example record with relation configuration
Scenario Outline: Accessors are generated for relations
  Given a record "Person" that has a "<multiplicity>" relation to a record "Address"
  When the "Person" class is generated
  Then "Person" has a method getAddress which returns "<type>"

    Examples:
  | multiplicity | type          |
  | 1-1          | Address       |
  | 1-M          | List<Address> |
  | M-1          | Address       |