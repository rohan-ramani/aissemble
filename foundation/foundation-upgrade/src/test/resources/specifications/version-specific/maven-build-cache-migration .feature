Feature: Update Maven build cache configuration to avoid Docker bug

  The Maven build cache configuration as it existed in 1.12 can result in stale docker images. The default number of
  builds to save is 5, however restoring anything but the most recent build will result in Java artifacts rolling back,
  but not the actual Docker image stored in the daemon. This can cause invalid/misleading testing.

  Scenario: Run migration if max builds is not 1
    Given a Maven build cache config with max builds set higher than one
    When the 1.13.0 Maven build cache migration executes
    Then the config is updated to set max builds to one

  Scenario: Skip migration if max builds is already 1
    Given a Maven build cache config with max builds set to one
    When the 1.13.0 Maven build cache migration executes
    Then the build cache migration is skipped