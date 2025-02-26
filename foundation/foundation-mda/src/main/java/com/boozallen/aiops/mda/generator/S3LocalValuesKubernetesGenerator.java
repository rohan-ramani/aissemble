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

import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.python.MachineLearningPipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

public class S3LocalValuesKubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                         | Generated File               |
     * |----------------------|--------------------------------------------------|------------------------------|
     * | s3LocalValuesFileV2  | deployment/localstack/localstack.values.yaml.vm  | apps/${appName}/values.yaml  |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();
        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        for (Pipeline pipeline: pipelineMap.values()) {
            String pipelineType = pipeline.getType().getName();
            if (PipelineEnum.DATA_FLOW.equalsIgnoreCase(pipelineType)) {
                // pyspark/spark pipelines
                vc.put(VelocityProperty.DATAFLOW_PIPELINES, true);
            } else if (PipelineEnum.MACHINE_LEARNING.equalsIgnoreCase(pipelineType)) {
                // training/inference pipelines
                vc.put(VelocityProperty.MACHINE_LEARNING_PIPELINES, true);
            }
        }


        generateFile(context, vc);
    }

}
