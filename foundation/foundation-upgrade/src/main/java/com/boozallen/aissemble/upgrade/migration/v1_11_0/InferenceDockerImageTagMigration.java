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

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration include correct the inference helm template docker image configuration to use the maven built docker image
 */
public class InferenceDockerImageTagMigration extends AbstractPomMigration {

    @Override
    protected boolean shouldExecuteOnFile(File file) {

        if (isInferenceDevYaml(file)) {
            try {
                YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
                if (yaml.hasObject("image")) {
                    YamlUtils.YamlObject image = yaml.getObject("image");
                    return !image.containsKey("tag") || !image.containsKey("imagePullPolicy");
                }
                return true;
            } catch (Exception e) {
                throw new BatonException("Failed to read an inference yaml file: " + file.getPath(), e);
            }
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            Map<String, String> stepArtifactsIds = getStepArtifactIds();
            String deployAppName = file.getParentFile().getName();
            String version = stepArtifactsIds.get(deployAppName);
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedContent = new ArrayList<>();
            boolean updated = false;
            final String image = "image:";
            // get the indentSpaces spaces
            int indentSpaces = YamlUtils.getIndentSpaces(lines, 0);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).stripTrailing();
                updatedContent.add(line);
                if (line.equals(image)) {

                    if (!imageHasNoConfigOf(lines, i, "imagePullPolicy:")) {
                        updatedContent.add(getImagePullPolicy(indentSpaces));
                    }
                    if (!imageHasNoConfigOf(lines, i, "tag:")) {
                        updatedContent.add(getImageTag(indentSpaces, version));
                    }
                    updated = true;
                }
            }
            if (!updated) {
                updatedContent.add(image);
                updatedContent.add(getImageTag(indentSpaces, version));
                updatedContent.add(getImagePullPolicy(indentSpaces));
            }
            writeFile(file, updatedContent);
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to remove `image_tag=latest` config in: " + file.getPath(), e);
        }
    }

    private String getImagePullPolicy(int indentSpaces) {
        return indent(1, indentSpaces) + "imagePullPolicy: IfNotPresent";
    }

    private String getImageTag(int indentSpaces, String version) {
        return String.format(indent(1, indentSpaces) + "tag: \"%s\"", version);
    }

    protected Map<String, String> getStepArtifactIds() {
        Map<String, String> inferenceProjects= new HashMap<>();
        for (MavenProject project : getRootProject().getCollectedProjects()) {
            Plugin plugin = project.getPlugin(Plugin.constructKey("org.technologybrewery.fermenter", "fermenter-mda"));
            String pluginConfigProfile = getConfigProfileValue(plugin);
            if( pluginConfigProfile != null && pluginConfigProfile.equals("machine-learning-inference")) {
                inferenceProjects.put(project.getArtifactId(), project.getParent().getVersion());
                break;
            }
        }
        return inferenceProjects;
    }

    private static String getConfigProfileValue(Plugin plugin) {
        if (plugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
            if (configuration != null) {
                if (configuration.getChild("profile") != null) {
                    return configuration.getChild("profile").getValue();
                }
            }
        }
        return null;
    }

    private boolean isInferenceDevYaml(File file) {
        Map<String, String> stepArtifactsIds = getStepArtifactIds();
        String deployAppName = file.getParentFile().getName();
        return stepArtifactsIds.keySet().contains(deployAppName);
    }

    private boolean imageHasNoConfigOf(List<String> lines, int imageIndex, String configName) {
        int i = imageIndex + 1;
        String line = lines.get(i).stripTrailing();

        while (!StringUtils.isBlank(line) && line.startsWith(" ")) {
            if (line.trim().startsWith(configName)) {
                return true;
            }
            line = lines.get(++i);
        }
        return false;
    }
}
