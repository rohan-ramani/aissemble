# Major Additions

## Service account support for spark-infrastructure
To have a more flexible and secure way to authenticate with AWS services, the spark-infrastructure helm chart has been enhanced to support [AWS IRSA](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html) (IAM Roles for Service Accounts) authentication. See the _**How to Upgrade**_ for more information.

## Path to Production Alignment
To better align development processes with processes in CI/CD and higher environments, we no longer recommend using Tilt live-reloading.  As such, upgrading projects should consider narrowing the scope of their Tiltfile. These changes will also help smooth the transition as further alignment is brought to the path to production. See _**How to Upgrade**_ for more information.

## Data Access Upgrade
Data access through [GraphQL](https://graphql.org/) has been deprecated and replaced with [Trino](https://trino.io/). Trino is optimized for performing queries against large datasets by leveraging a distributed architecture that processes queries in parallel, enabling fast and scalable data retrieval.

## Spark Upgrade
Spark and PySpark have been upgraded from version 3.5.2 to 3.5.4.

## Record Relation
To enable nested data records, we have added a new relation feature to the record metamodel. This allows records to reference other records. For more details, refer to the [Record Relation Options](https://boozallen.github.io/aissemble/aissemble/current-dev/record-metamodel.html#_record_relation_options).
Several features are still a work in progress:
- PySpark and Spark schema based validation for relations will only validate the record and not its relations. Object based validation for relations is available.

## Helm Charts Resource Specification
The following Helm charts have been updated to include the configuration options for specifying container resource requests/limits:
- `aissemble-spark-history-chart`
- `aissemble-quarkus-chart`

See the[official Kubernetes documentation](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) for more details.

# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

 - The following Java classes have been renamed:
   | Old Java Class                | New Java Class                     |
   |-------------------------------|------------------------------------|
   | `AIOpsModelInstanceRepostory` | `AissembleModelInstanceRepository` |
   | `AiopsMdaJsonUtils`           | `AissembleMdaJsonUtils`            |
 - To improve the development cycle and docker build consistency, we have deprecated the docker_build() and local_resources() functions in the Tilt and enable maven docker build for the docker modules. Follow the instruction in the `Finalizing the Upgrade` to avoid duplicated docker image build.
 - In an attempt to harden the `aissemble-hive-service` image, several changes were made that may impact projects with Hive customization


# Known Issues

## Docker Module Build Failures
When using a Docker daemon that does not reside in `/var/run` (e.g. running Rancher Desktop without admin privileges) the docker-maven-plugin will fail to build with the message below. To work around this failure, set the `DOCKER_HOST` variable to the location of the daemon socket file. For example, to make the docker-maven-plugin work with Rancher Desktop, run `export DOCKER_HOST=unix://$HOME/.rd/docker.sock`.

```shell
[ERROR] Failed to execute goal org.technologybrewery.fabric8:docker-maven-plugin:0.45-tb-0.1.0:build (default-build) on project final-513-spark-worker-docker:
   Execution default-build of goal org.technologybrewery.fabric8:docker-maven-plugin:0.45-tb-0.1.0:build failed: 
   No <dockerHost> given, no DOCKER_HOST environment variable, no read/writable '/var/run/docker.sock' or '//./pipe/docker_engine' and no external provider like Docker machine configured
```

# Known Vulnerabilities

| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|


# How to Upgrade

The following steps will upgrade your project to `1.11`. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                     | Description                                                                                                                                                                             |
|----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration       | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                            |
| upgrade-v2-chart-files-aissemble-version-migration | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE            |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                  |
| pipeline-invocation-service-template-migrtion      | Include the helm.valueFiles param to ArgoCD pipeline-invocation-service template                                                                                                        |                                                                                                                                                      
| docker-module-pom-dependency-type-migration        | Updates the maven pipeline dependency type within your project's sub docker module pom file(`<YOUR_PROJECT>-docker/*-docker/pom.xml`) to fix the build cache checksum calculation issue |
| enable-maven-docker-build-migration                | Remove the maven fabric8 plugin `skip` configuration within your project's docker module pom file(`<YOUR_PROJECT>-docker/pom.xml`) to enable the maven docker build                     |
| ml-pipeline-docker-pom-migration                   | Adds pipeline ML pipeline dependencies to relevant docker POMs to improve the Maven build cache functionality                                                                           |
| training-api-image-tag-migration                   | Update training docker image tags to use project version                                                                                                                                |
| inference-docker-image-tag-migration               | Update inference docker image tags to use project version                                                                                                                               |
| spark-worker-docker-image-tag-migration            | Updates Spark docker image tags to use project version                                                                                                                                  |
| spark-infrastructure-server-side-label-migration   | Enables the Server-Side Diff Strategy within ArgoCD for the Spark Infrastructure resource so that changes made by the Universal Config Store mutating webhook are ignored               |
| data-access-default-migration                      | Migrates Record metamodels that were relying on the default Data Access settings to preserve semantics with the updated default value                                                   |

To deactivate any of these migrations, add the following configuration to the `baton-maven-plugin` within your root `pom.xml`:

```diff
    <plugin>
        <groupId>org.technologybrewery.baton</groupId>
        <artifactId>baton-maven-plugin</artifactId>
        <dependencies>
            <dependency>
                <groupId>com.boozallen.aissemble</groupId>
                <artifactId>foundation-upgrade</artifactId>
                <version>${version.aissemble}</version>
            </dependency>
        </dependencies>
+        <configuration>
+             <deactivateMigrations>
+                 <deactivateMigration>NAME_OF_MIGRATION</deactivateMigration>
+                 <deactivateMigration>NAME_OF_MIGRATION</deactivateMigration>
+             </deactivateMigrations>
+        </configuration>
    </plugin>
```

## Precondition Steps - Required for All Projects

### Beginning the Upgrade
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.11.0 version of the build-parent:
```xml
<parent>
    <groupId>com.boozallen.aissemble</groupId>
    <artifactId>build-parent</artifactId>
    <version>1.11.0</version>
</parent>
```

### Tilt Docker Builds
To avoid duplicate docker builds, remove all the related `docker_build()` and `local_resources()` functions from your Tiltfile. Also, the `spark-worker-image.yaml` is no longer used so the `-deploy/src/main/resources/apps/spark-worker-image` directory and the related `k8s_yaml()` function from your Tiltfile can be removed.

## Conditional Steps

### For projects that have customized the Hive service
Several changes were made to both the Hive service Docker image and the Hive service chart included as part of the Spark Infrastructure chart of a project. The defaults have been adjusted so that these changes should be transparent, however due to the nature of some possible customizations this may not always hold true. The following changes may impact the function of your customizations and may need to be accounted for:
 - The image is now only the Hive Standalone Metastore service and cannot function as a full [Hive Server](https://hive.apache.org/development/quickstart/)
 - The Java installation at `/opt/java` is no longer symlinked to `/opt/jre` -- `JAVA_HOME` has been adjusted accordingly by default
 - The default working directory for the `aissemble-hive-service` image was changed from `/opt` to `/opt/hive`
 - Schema initialization is no longer done as part of an `initContainer` in the `aissemble-hive-service-chart` and is instead done in a new `entrypoint` script. This is consistent with the [official `apache/hive` Docker image](https://hub.docker.com/r/apache/hive).

### AWS IRSA (IAM Roles Service Account) Authentication
This is not a required step but a recommended way to authenticate AWS service
1. [Create an IAM OIDC provider for your cluster](https://docs.aws.amazon.com/eks/latest/userguide/enable-iam-roles-for-service-accounts.html)
2. Follow the [Assign IAM roles to Kubernetes service accounts](https://docs.aws.amazon.com/eks/latest/userguide/associate-service-account-role.html) document but **skip** the step that creates the service account
3. In the spark-infrastructure chart template, add the service account create configuration as below:
   
aissemble-spark-history-chart
```yaml
aissemble-spark-history-chart:
  serviceAccount:
    name: service-account-name
    enabled: true
    metadata:
      annotations:
        # Ref: IAM roles arn from step 2
        eks.amazonaws.com/role-arn: arn:aws:iam::aws-id:role/iam-role-name 
```

aissemble-thrift-server-chart:
```yaml
aissemble-thrift-server-chart:
  deployment:
    # service account name must match the service account name specified in the IAM roles trust relationships
    serviceAccountName: service-account-name
```

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
_to be auto-generated when published_
