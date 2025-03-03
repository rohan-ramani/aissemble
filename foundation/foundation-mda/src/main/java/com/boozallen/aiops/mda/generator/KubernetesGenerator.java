package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class KubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                      | Template                                                                                  | Generated File                                 |
     * |---------------------------------------------|-------------------------------------------------------------------------------------------|------------------------------------------------|
     * | airflowHelmChartFileV2                      | deployment/airflow/v2/airflow.chart.yaml.vm                                               | apps/${appName}/Chart.yaml                     |
     * | airflowValuesDevFileV2                      | deployment/airflow/v2/airflow.values-dev.yaml.vm                                          | apps/${appName}/values-dev.yaml                |
     * | airflowValuesFileV2                         | deployment/airflow/v2/airflow.values.yaml.vm                                              | apps/${appName}/values.yaml                    |
     * | bomArgoCD                                   | deployment/argocd/bom.yaml.vm                                                             | templates/bom.yaml                             |
     * | inferenceArgoCD                             | deployment/argocd/inference.yaml.vm                                                       | templates/${appName}.yaml                      |
     * | modelTrainingApiArgoCD                      | deployment/argocd/model-training-api.yaml.vm                                              | templates/model-training-api.yaml              |
     * | neo4jArgoCD                                 | deployment/argocd/neo4j.yaml.vm                                                           | templates/neo4j.yaml                           |
     * | nexusArgoCD                                 | deployment/argocd/nexus.yaml.vm                                                           | templates/nexus.yaml                           |
     * | postgresArgoCD                              | deployment/argocd/postgres.yaml.vm                                                        | templates/postgres.yaml                        |
     * | sharedInfrastructureArgoCD                  | deployment/argocd/shared-infrastructure.yaml.vm                                           | templates/shared-infrastructure.yaml           |
     * | airflowArgoCDV2                             | deployment/argocd/v2/airflow.yaml.vm                                                      | templates/airflow.yaml                         |
     * | aissembleConfigurationStoreArgoCDV2         | deployment/argocd/v2/configuration-store.yaml.vm                                          | templates/${appName}.yaml                      |
     * | elasticsearchOperatorArgoCDV2               | deployment/argocd/v2/elasticsearch.operator.yaml.vm                                       | templates/elasticsearch-operator.yaml          |
     * | elasticsearchArgoCDV2                       | deployment/argocd/v2/elasticsearch.yaml.vm                                                | templates/elasticsearch.yaml                   |
     * | inferenceArgoCDV2                           | deployment/argocd/v2/inference.yaml.vm                                                    | templates/airflow.yaml                         |
     * | kafkaArgoCDV2                               | deployment/argocd/v2/kafka.yaml.vm                                                        | templates/kafka.yaml                           |
     * | keycloakArgoCDV2                            | deployment/argocd/v2/keycloak.yaml.vm                                                     | templates/keycloak.yaml                        |
     * | lineageCustomConsumerArgoCD                 | deployment/argocd/v2/lineage.custom.consumer.yaml.vm                                      | templates/lineage-custom-consumer.yaml         |
     * | lineageHttpConsumerArgoCD                   | deployment/argocd/v2/lineage.http.consumer.yaml.vm                                        | templates/lineage-http-consumer.yaml           |
     * | metadataArgoCDV2                            | deployment/argocd/v2/metadata.yaml.vm                                                     | templates/metadata.yaml                        |
     * | mlflowArgoCDV2                              | deployment/argocd/v2/mlflow-ui.yaml.vm                                                    | templates/mlflow-ui.yaml                       |
     * | pipelineInvocationServiceArgoCD-v2          | deployment/argocd/v2/pipeline.invocation.service.yaml.vm                                  | templates/pipeline-invocation-service.yaml     |
     * | policyDecisionPointArgoCDV2                 | deployment/argocd/v2/policy-decision-point.yaml.vm                                        | templates/policy-decision-point.yaml           |
     * | aissembleQuarkusArgoCD                      | deployment/argocd/v2/quarkus.yaml.vm                                                      | templates/${appName}.yaml                      |
     * | s3LocalArgoCDV2                             | deployment/argocd/v2/s3-local.yaml.vm                                                     | templates/s3-local.yaml                        |
     * | sparkInfrastructureArgoCDFileV2             | deployment/argocd/v2/spark.infrastructure.yaml.vm                                         | templates/spark-infrastructure.yaml            |
     * | sparkOperatorArgoCDV2                       | deployment/argocd/v2/spark.operator.yaml.vm                                               | templates/spark-operator.yaml                  |
     * | trinoArgoCDV2                               | deployment/argocd/v2/trino.yaml.vm                                                        | templates/trino-ui.yaml                        |
     * | versioningArgoCDV2                          | deployment/argocd/v2/versioning.yaml.vm                                                   | templates/versioning.yaml                      |
     * | vaultArgoCD                                 | deployment/argocd/vault.yaml.vm                                                           | templates/vault.yaml                           |
     * | bomValuesDevFile                            | deployment/bom/bom.values-dev.yaml.vm                                                     | apps/${appName}/values-dev.yaml                |
     * | bomValuesFile                               | deployment/bom/bom.values.yaml.vm                                                         | apps/${appName}/values.yaml                    |
     * | aissembleConfigurationStoreHelmChartFileV2  | deployment/configuration-store/v2/configuration-store.chart.yaml.vm                       | apps/${appName}/Chart.yaml                     |
     * | aissembleConfigurationStoreValuesDevFileV2  | deployment/configuration-store/v2/configuration-store.values-dev.yaml.vm                  | apps/${appName}/values-dev.yaml                |
     * | aissembleConfigurationStoreValuesFileV2     | deployment/configuration-store/v2/configuration-store.values.yaml.vm                      | apps/${appName}/values.yaml                    |
     * | elasticsearchOperatorHelmChartFileV2        | deployment/elasticsearch-operator/v2/elasticsearch.operator.chart.yaml.vm                 | apps/${appName}/Chart.yaml                     |
     * | elasticsearchOperatorValuesDevFileV2        | deployment/elasticsearch-operator/v2/elasticsearch.operator.values-dev.yaml.vm            | apps/${appName}/values-dev.yaml                |
     * | elasticsearchOperatorValuesFileV2           | deployment/elasticsearch-operator/v2/elasticsearch.operator.values.yaml.vm                | apps/${appName}/values.yaml                    |
     * | elasticsearchHelmChartFileV2                | deployment/elasticsearch/v2/elasticsearch.chart.yaml.vm                                   | apps/${appName}/Chart.yaml                     |
     * | elasticsearchValuesDevFileV2                | deployment/elasticsearch/v2/elasticsearch.values-dev.yaml.vm                              | apps/${appName}/values-dev.yaml                |
     * | elasticsearchValuesFileV2                   | deployment/elasticsearch/v2/elasticsearch.values.yaml.vm                                  | apps/${appName}/values.yaml                    |
     * | helmChartFile                               | deployment/helm/chart.yaml.vm                                                             | apps/${appName}/Chart.yaml                     |
     * | helmDeploymentFile                          | deployment/helm/deployment.yaml.vm                                                        | apps/${appName}/templates/deployment.yaml      |
     * | helmIgnoreFile                              | deployment/helm/helmignore.vm                                                             | apps/${appName}/.helmignore                    |
     * | helmIngressFile                             | deployment/helm/ingress.yaml.vm                                                           | apps/${appName}/templates/ingress.yaml         |
     * | helmServiceFile                             | deployment/helm/service.yaml.vm                                                           | apps/${appName}/templates/service.yaml         |
     * | inferenceValuesDevFile                      | deployment/inference/inference.values-dev.yaml.vm                                         | apps/${appName}/values-dev.yaml                |
     * | inferenceHelmChartFileV2                    | deployment/inference/v2/inference.chart.yaml.vm                                           | apps/${appName}/Chart.yaml                     |
     * | kafkaHelmChartFileV2                        | deployment/kafka/v2/kafka.chart.yaml.vm                                                   | apps/${appName}/Chart.yaml                     |
     * | kafkaValuesDevFileV2                        | deployment/kafka/v2/kafka.values-dev.yaml.vm                                              | apps/${appName}/values-dev.yaml                |
     * | kafkaValuesFileV2                           | deployment/kafka/v2/kafka.values.yaml.vm                                                  | apps/${appName}/values.yaml                    |
     * | keycloakChartFileV2                         | deployment/keycloak/v2/keycloak.chart.yaml.vm                                             | apps/${appName}/Chart.yaml                     |
     * | keycloakValuesDevFileV2                     | deployment/keycloak/v2/keycloak.values-dev.yaml.vm                                        | apps/${appName}/values-dev.yaml                |
     * | keycloakValuesFileV2                        | deployment/keycloak/v2/keycloak.values.yaml.vm                                            | apps/${appName}/values.yaml                    |
     * | lineageCustomConsumerHelmChartFile          | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.chart.yaml.vm               | apps/${appName}/Chart.yaml                     |
     * | lineageCustomConsumerValuesDevFile          | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.values-dev.yaml.vm          | apps/${appName}/values-dev.yaml                |
     * | lineageCustomConsumerValuesFile             | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.values.yaml.vm              | apps/${appName}/values.yaml                    |
     * | lineageHttpConsumerHelmChartFile            | deployment/lineage-http-consumer/v2/lineage.http.consumer.chart.yaml.vm                   | apps/${appName}/Chart.yaml                     |
     * | lineageHttpConsumerValuesDevFile            | deployment/lineage-http-consumer/v2/lineage.http.consumer.values-dev.yaml.vm              | apps/${appName}/values-dev.yaml                |
     * | lineageHttpConsumerValuesFile               | deployment/lineage-http-consumer/v2/lineage.http.consumer.values.yaml.vm                  | apps/${appName}/values.yaml                    |
     * | s3LocalChartFileV2                          | deployment/localstack/localstack.chart.yaml.vm                                            | apps/${appName}/Chart.yaml                     |
     * | s3LocalValuesDevFileV2                      | deployment/localstack/localstack.values.dev.yaml.vm                                       | apps/${appName}/values-dev.yaml                |
     * | metadataHelmChartFileV2                     | deployment/metadata/v2/metadata.chart.yaml.vm                                             | apps/${appName}/Chart.yaml                     |
     * | metadataValuesDevFileV2                     | deployment/metadata/v2/metadata.values-dev.yaml.vm                                        | apps/${appName}/values-dev.yaml                |
     * | metadataValuesFileV2                        | deployment/metadata/v2/metadata.values.yaml.vm                                            | apps/${appName}/values.yaml                    |
     * | mlflowHelmChartFileV2                       | deployment/mlflow/v2/mlflow.chart.yaml.vm                                                 | apps/${appName}/Chart.yaml                     |
     * | mlflowValuesDevFileV2                       | deployment/mlflow/v2/mlflow.values-dev.yaml.vm                                            | apps/${appName}/values-dev.yaml                |
     * | mlflowValuesFileV2                          | deployment/mlflow/v2/mlflow.values.yaml.vm                                                | apps/${appName}/values.yaml                    |
     * | modelTrainingApiConfigMapFile               | deployment/model-training-api/model-training-api.configmap.yaml.vm                        | apps/${appName}/templates/configmap.yaml       |
     * | modelTrainingApiDeploymentFile              | deployment/model-training-api/model-training-api.deployment.yaml.vm                       | apps/${appName}/templates/deployment.yaml      |
     * | modelTrainingApiRbacFile                    | deployment/model-training-api/model-training-api.rbac.yaml.vm                             | apps/${appName}/templates/rbac.yaml            |
     * | modelTrainingApiServiceAccountFile          | deployment/model-training-api/model-training-api.serviceaccount.yaml.vm                   | apps/${appName}/templates/serviceaccount.yaml  |
     * | modelTrainingApiValuesDevFile               | deployment/model-training-api/model-training-api.values-dev.yaml.vm                       | apps/${appName}/values-dev.yaml                |
     * | modelTrainingApiValuesFile                  | deployment/model-training-api/model-training-api.values.yaml.vm                           | apps/${appName}/values.yaml                    |
     * | neo4jValuesDevFile                          | deployment/neo4j/neo4j.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                |
     * | neo4jValuesFile                             | deployment/neo4j/neo4j.values.yaml.vm                                                     | apps/${appName}/values.yaml                    |
     * | nexusValuesDevFile                          | deployment/nexus/nexus.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                |
     * | nexusValuesFile                             | deployment/nexus/nexus.values.yaml.vm                                                     | apps/${appName}/values.yaml                    |
     * | pipelineInvocationServiceHelmChartFile-v2   | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.chart.yaml.vm       | apps/${appName}/Chart.yaml                     |
     * | pipelineInvocationServiceConfigMapFile-v2   | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.config.map.yaml.vm  | apps/${appName}/templates/configmap.yaml       |
     * | pipelineInvocationServiceValuesDevFile-v2   | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.values-dev.yaml.vm  | apps/${appName}/values-dev.yaml                |
     * | pipelineInvocationServiceValuesFile-v2      | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.values.yaml.vm      | apps/${appName}/values.yaml                    |
     * | policyDecisionPointHelmChartFileV2          | deployment/policy-decision-point/v2/policy.decision.point.chart.yaml.vm                   | apps/${appName}/Chart.yaml                     |
     * | policyDecisionPointValuesDevFileV2          | deployment/policy-decision-point/v2/policy.decision.point.values-dev.yaml.vm              | apps/${appName}/values-dev.yaml                |
     * | policyDecisionPointValuesFileV2             | deployment/policy-decision-point/v2/policy.decision.point.values.yaml.vm                  | apps/${appName}/values.yaml                    |
     * | postgresValuesDevFile                       | deployment/postgres/postgres.values-dev.yaml.vm                                           | apps/${appName}/values-dev.yaml                |
     * | postgresValuesFile                          | deployment/postgres/postgres.values.yaml.vm                                               | apps/${appName}/values.yaml                    |
     * | aissembleQuarkusHelmChartFile               | deployment/quarkus/quarkus.chart.yaml.vm                                                  | apps/${appName}/Chart.yaml                     |
     * | aissembleQuarkusValuesDevFile               | deployment/quarkus/quarkus.values-dev.yaml.vm                                             | apps/${appName}/values-dev.yaml                |
     * | aissembleQuarkusValuesFile                  | deployment/quarkus/quarkus.values.yaml.vm                                                 | apps/${appName}/values.yaml                    |
     * | sharedInfrastructureValuesDevFile           | deployment/shared-infrastructure/shared-infrastructure.values-dev.yaml.vm                 | apps/${appName}/values-dev.yaml                |
     * | sharedInfrastructureValuesFile              | deployment/shared-infrastructure/shared-infrastructure.values.yaml.vm                     | apps/${appName}/values.yaml                    |
     * | sparkInfrastructureHelmChartFileV2          | deployment/spark-infrastructure/v2/spark.infrastructure.chart.yaml.vm                     | apps/${appName}/Chart.yaml                     |
     * | sparkInfrastructureHelmValuesDevFileV2      | deployment/spark-infrastructure/v2/spark.infrastructure.values.dev.yaml.vm                | apps/${appName}/values-dev.yaml                |
     * | sparkInfrastructureHelmValuesFileV2         | deployment/spark-infrastructure/v2/spark.infrastructure.values.yaml.vm                    | apps/${appName}/values.yaml                    |
     * | sparkOperatorChartFileV2                    | deployment/spark-operator/v2/spark.operator.chart.yaml.vm                                 | apps/${appName}/Chart.yaml                     |
     * | sparkOperatorValuesDevFileV2                | deployment/spark-operator/v2/spark.operator.values.dev.yaml.vm                            | apps/${appName}/values-dev.yaml                |
     * | sparkOperatorValuesFileV2                   | deployment/spark-operator/v2/spark.operator.values.yaml.vm                                | apps/${appName}/values.yaml                    |
     * | trinoHelmChartFileV2                        | deployment/trino/v2/trino.chart.yaml.vm                                                   | apps/${appName}/Chart.yaml                     |
     * | trinoValuesDevFileV2                        | deployment/trino/v2/trino.values-dev.yaml.vm                                              | apps/${appName}/values-dev.yaml                |
     * | trinoValuesFileV2                           | deployment/trino/v2/trino.values.yaml.vm                                                  | apps/${appName}/values.yaml                    |
     * | valuesCIFile                                | deployment/values-ci.yaml.vm                                                              | apps/${appName}/values-ci.yaml                 |
     * | vaultValuesDevFile                          | deployment/vault/vault.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                |
     * | vaultValuesFile                             | deployment/vault/vault.values.yaml.vm                                                     | apps/${appName}/values.yaml                    |
     * | versioningHelmChartFileV2                   | deployment/versioning/v2/versioning.chart.yaml.vm                                         | apps/${appName}/Chart.yaml                     |
     * | versioningValuesDevFileV2                   | deployment/versioning/v2/versioning.values-dev.yaml.vm                                    | apps/${appName}/values-dev.yaml                |
     * | versioningValuesFileV2                      | deployment/versioning/v2/versioning.values.yaml.vm                                        | apps/${appName}/values.yaml                    |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        generateFile(context, vc);
    }

}
