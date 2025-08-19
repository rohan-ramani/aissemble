package com.boozallen.aissemble.maven.enforcer.testenforcers;

import java.net.URI;

import com.boozallen.aissemble.maven.enforcer.EnforceDockerExecutable;

public class EnforceDockerExecutableTest extends EnforceDockerExecutable {
    private final String os;
    private final String dockerHost;
    private final String dockerContextName;
    private final String dockerContextUrl;
    private final String defaultDockerContextUrl;
    private final boolean isUnixPath;

    public EnforceDockerExecutableTest(String os, String dockerHost, String dockerContextName,
                                       String dockerContextUrl, String defaultDockerContextUrl, boolean isUnixPath) {
        this.os = os;
        this.dockerHost = dockerHost;
        this.dockerContextName = dockerContextName;
        this.dockerContextUrl = dockerContextUrl;
        this.defaultDockerContextUrl = defaultDockerContextUrl;
        this.isUnixPath = isUnixPath;
    }

    @Override
    protected boolean isUnix() {
        return os.equals("unix");
    }

    @Override
    protected String getDockerHost() {
        return this.dockerHost;
    }

    @Override
    protected String getDockerContextName() {
        return this.dockerContextName;
    }

    @Override
    protected String getDockerContextUrl(String contextName) {
        if (contextName.equalsIgnoreCase("default")) {
            return this.defaultDockerContextUrl;
        }
        return this.dockerContextUrl;
    }

    @Override
    protected boolean isUriNonUnix(URI uri) {
        return !isUnixPath;
    }
}
