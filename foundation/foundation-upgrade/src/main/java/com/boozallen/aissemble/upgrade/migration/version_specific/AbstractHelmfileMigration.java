package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.commons.lang3.StringUtils;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

public abstract class AbstractHelmfileMigration extends AbstractAissembleMigration {

    public static final String HELMFILE_MIGRATION_ENABLE_KEY = "aissemble.enable.helmfile.migration";

    public boolean isHelmfileMigrationActive() {
        return StringUtils.equalsIgnoreCase("true", System.getProperty(HELMFILE_MIGRATION_ENABLE_KEY));
    }
}
