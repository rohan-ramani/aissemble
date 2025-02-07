Feature: Record Metamodels Migrated for new Data Access default

  The default value for `dataAccess.enabled` is changing from `true` to `false`, so existing records need to be updated
  to preserve their current semantics.

  Scenario: Migrate a Record with `dataAccess.enabled` explicitly set
    Given a record with dataAccess.enabled set explicitly
    When the data access default migration executes
    Then the data access default migration is skipped

  Scenario: Migrate a Record without `dataAccess` specified
    Given a record with dataAccess undefined
    When the data access default migration executes
    Then the record is updated to set dataAccess.enabled to true

  Scenario: Migrate a Record without `dataAccess.enabled` specified
    Given a record with dataAccess defined but enabled undefined
    When the data access default migration executes
    Then the record is updated to set dataAccess.enabled to true

