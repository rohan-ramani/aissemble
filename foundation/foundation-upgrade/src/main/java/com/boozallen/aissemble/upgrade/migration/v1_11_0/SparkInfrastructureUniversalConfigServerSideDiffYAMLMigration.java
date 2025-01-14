package com.boozallen.aissemble.upgrade.migration.v1_11_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration enables Server-Side Diff Strategy within ArgoCD for the Spark Infrastructure resource so that changes made by the Universal Config Store mutating webhook are ignored in ArgoCD diffs.
 */
public class SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration.class);
    private static final String ANNOTATIONS = "annotations:";
    private static final String METADATA = "metadata:";
    private static final String SERVERSIDE_DIFF_VALUES = "argocd.argoproj.io/compare-options: ServerSideDiff=true,IncludeMutationWebhook=true";
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            List<String> content = readAllFileLines(file);
            for (String line : content) {
                //if annotation is already there, skip migration.
                if (line.trim().contains(SERVERSIDE_DIFF_VALUES.trim())) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new BatonException("Failed to evaluate pipeline invocation service ArgoCD template.", e);
        }
        return true;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> updatedLine = new ArrayList<>();
            List<String> lines = readAllFileLines(file);
            int metadataIndex = 0;
            int indentSpaces = 0;
            boolean hasAnnotation = false;
            for (int i = 0; i < lines.size(); i++) {
                String trimmedLine = lines.get(i).trim();
                if(trimmedLine.contains(METADATA))
                {
                    metadataIndex = i;
                }
                updatedLine.add(lines.get(i));
                if(trimmedLine.contains(ANNOTATIONS)) {
                    hasAnnotation = true;
                    indentSpaces = YamlUtils.getIndentSpaces(lines, i + 1);
                    updatedLine.add(indent(1, indentSpaces) + SERVERSIDE_DIFF_VALUES);
                }
            }
            if(!hasAnnotation)
            {
                int annotationIdx = metadataIndex+1;
                indentSpaces = YamlUtils.getIndentSpaces(lines, annotationIdx);
                updatedLine.add(annotationIdx, indent(1, indentSpaces) + ANNOTATIONS);
                updatedLine.add(annotationIdx+1, indent(2, indentSpaces) + SERVERSIDE_DIFF_VALUES);
            }

            writeFile(file, updatedLine);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
