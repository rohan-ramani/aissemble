package com.boozallen.aissemble.configuration.config;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

/**
 * Configurations for config store vault server access.
 */
@KrauseningConfig.KrauseningSources("config-store-vault.properties")
public interface ConfigStoreVaultConfiguration extends KrauseningConfig {

    /**
     * Returns the URL to the Vault server.
     * 
     * @return vault server url
     */
    @Key("secrets.host.url")
    @DefaultValue("http://127.0.0.1:8217")
    String getSecretsHostUrl();

    /**
     * Returns the root key for the Vault server.
     * 
     * @return vault root key
     */
    @Key("secrets.root.key")
    @DefaultValue("rootkey")
    String getSecretsRootKey();

    /**
     * Returns the unseal keys for the Vault server.
     * 
     * @return vault unseal keys
     */
    @Key("secrets.unseal.keys")
    @DefaultValue("key1,key2,key3")
    String getSecretsUnsealKeys();

}
