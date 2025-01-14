Feature: Migrate Spark Infrastructure Universal Configuration ServerSide Diff Migration
  This feature enables server side diff strategy for mutation webhook. We have plan to use Universal configuration Store that implements mutation webhook to inject
  values for various values in the spark infrastructure. Since ArgoCD directly compares local and deployed manifest, any values changed using
  mutation webhook will show out of sync. To resolve this issue, the server side diff strategy will sort out values injected by mutation webhook and will show it as sync.

Scenario: Spark infrastructure application file with annotation block is migrated
  Given spark infrastructure yaml file that doesn't have server-side diff annotation but has annotation block
  When the spark infrastructure configuration server-side diff migration executes
  Then spark-infrastructure.yaml is updated to add server-side diff annotation.

Scenario: Spark infrastructure application file without annotation block is migrated
  Given spark infrastructure yaml file that doesn't have annotation block
  When the spark infrastructure configuration server-side diff migration executes
  Then spark-infrastructure.yaml is updated to add server-side diff annotation with annotation block.

Scenario: Spark infrastructure application file is skipped
  Given spark infrastructure yaml file that has server-side diff annotation
  When the spark infrastructure configuration server-side diff migration executes
  Then the spark infrastructure configuration server-side diff migration is skipped