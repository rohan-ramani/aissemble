package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps determine the version of Helm that is available on the user's
 * {@code PATH}.
 */
public class HelmVersionHelper extends BaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(HelmVersionHelper.class);

    private static final String HELM_COMMAND = "helm";
    private static final String EXTRACT_VERSION_REGEX = "Version:\"v(.*?)\"";

    public HelmVersionHelper(File workingDirectory) {
        super(workingDirectory, HELM_COMMAND);
    }

    /**
     * Retrieves the version of Helm that is set for the configured working
     * directory.
     *
     * @return
     */
    public String getCurrentHelmVersion() throws ShellExecutionException {
        String version = quietlyExecute(Collections.singletonList("version"));
        Pattern pattern = Pattern.compile(EXTRACT_VERSION_REGEX);
        Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new ShellExecutionException("helm version could not be found");
    }
}
