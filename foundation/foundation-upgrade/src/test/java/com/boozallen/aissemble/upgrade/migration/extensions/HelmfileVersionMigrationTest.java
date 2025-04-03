package com.boozallen.aissemble.upgrade.migration.extensions;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.HelmFileVersionMigration;

/**
 * Test class to override the desired aissemble version
 */
public class HelmfileVersionMigrationTest extends HelmFileVersionMigration {

    @Override
    public String getAissembleVersion() {
        return "1.12.1";
    }
}
