package com.boozallen.aissemble.maven.enforcer.helper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DockerExecutableHelper extends BaseHelper {
    private static final String DOCKER_COMMAND = "docker";
    private static final String CONTEXT_HOST_FORMAT = "{{.Endpoints.docker.Host}}";

    public DockerExecutableHelper(File workingDirectory) {
        super(workingDirectory, DOCKER_COMMAND);
    }

    /**
     * Gets the current docker context name and host url
     *
     * @return A string map with name, host url
     */
    public String getDockerContextName() {
        List<String> command = Arrays.asList("context", "show");
        return quietlyExecute(command);
    }

    public String getDockerContextUrl(String contextName) {
        if (StringUtils.isNotEmpty(contextName)) {
            List<String> command = Arrays.asList("context", "inspect", contextName, "--format", CONTEXT_HOST_FORMAT);
            return quietlyExecute(command);
        }
        return null;
    }
}
