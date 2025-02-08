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

  Scenario: POM is updated to disable this migration - existing inline migrations
    Given a root project POM file with inlined disabled migrations
    When the data access default migration executes
    Then the POM is updated to disable the "data-access-default-migration" migration

  Scenario: POM is updated to disable this migration - existing tag migrations
    Given a root project POM file with disabled migrations
    When the data access default migration executes
    Then the POM is updated to disable the "data-access-default-migration" migration

  Scenario: POM is updated to disable this migration - with configuration
    Given a root project POM file with a Baton configuration
    When the data access default migration executes
    Then the POM is updated to disable the "data-access-default-migration" migration

  Scenario: POM is updated to disable this migration - default
    Given a root project POM file
    When the data access default migration executes
    Then the POM is updated to disable the "data-access-default-migration" migration

  Scenario: POM is updated to disable this migration - already migrated inline
    Given a root project POM file with this migration disabled inline
    When the data access default migration executes
    Then the data access default migration is skipped

  Scenario: POM is updated to disable this migration - already migrated tag
    Given a root project POM file with this migration disabled
    When the data access default migration executes
    Then the data access default migration is skipped
