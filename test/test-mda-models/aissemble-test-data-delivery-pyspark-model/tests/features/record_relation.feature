@relation
Feature: Example record with relation configuration
Scenario Outline: Accessors are generated for relations
  Given a record "Person" that has a "<multiplicity>" relation to a record "Address"
  When the "Person" class is generated
  Then "Person" has a method address which returns "<type>"

  Examples:
  | multiplicity | type          |
  | 1-1          | Address       |
  | 1-M          | List[Address] |
  | M-1          | Address       |

Scenario Outline: Records with relations can be serialized as JSON strings
  Given a record "Person" that has a "<multiplicity>" relation to a record "Address"
  When the record is serialized
  Then the record relations are maintained as JSON string

  Examples:
   | multiplicity |
   | 1-1          |  
   | 1-M          |
   | M-1          |

Scenario Outline: Records with relations can be deserialized as Record objects
  Given a JSON string that has a "<multiplicity>" record relation encoded
  When the JSON string is deserialized
  Then the record relations are maintained as a Record object

  Examples:
   | multiplicity |
   | 1-1          |
   | 1-M          |
   | M-1          |