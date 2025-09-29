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

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import org.apache.maven.model.Model;
import org.apache.maven.model.Properties;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;

/**
 * This migration updates Maven compiler source and target properties from Java 17 to Java 21
 * to support the platform upgrade to Java 21 LTS.
 */
public class Java21UpgradeMigration extends AbstractPomMigration {

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        Properties properties = model.getProperties();
        
        return hasJava17CompilerProperties(properties);
    }

    @Override
    protected boolean performMigration(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        Properties properties = model.getProperties();
        PomModifications modifications = new PomModifications();
        
        if ("17".equals(properties.getProperty("maven.compiler.source"))) {
            modifications.add(new PomModifications.PropertyUpdate("maven.compiler.source", "21"));
        }
        
        if ("17".equals(properties.getProperty("maven.compiler.target"))) {
            modifications.add(new PomModifications.PropertyUpdate("maven.compiler.target", "21"));
        }
        
        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }
    
    private boolean hasJava17CompilerProperties(Properties properties) {
        String compilerSource = properties.getProperty("maven.compiler.source");
        String compilerTarget = properties.getProperty("maven.compiler.target");
        
        return "17".equals(compilerSource) || "17".equals(compilerTarget);
    }
}
