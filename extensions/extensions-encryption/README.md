# extensions-encryption
Services used to encrypt data

### Vault encryption
In order to encrypt data the following properties need to be set in `encrypt.properties`. Note that the root and
unseal keys will change each time the Vault server is initialized.

* **secrets.host.url**=[Some Url]
  * Example: http://127.0.0.1:8217
* **secrets.unseal.keys**=[Comma delimeted set of keys]
  * Example (no quotes or whitespace): key1,key2,key3,key4,key5
* **secrets.root.key**=[The root key]

In a production system a System Administrator would start the Vault service and perform the unsealing, but for
demonstration purposes the java code will perform the unsealing with the provided keys.

You can check the vault console log.  The keys will be printed at the top

```
vault    | ROOT KEY
vault    | s.EXAMPLE
vault    | UNSEAL KEYS
vault    | ["key1", "key2", "key3", "key4", "key5"]
vault    | TRANSIT TOKEN
vault    | {"request_id": "29d26f42-7be2-9b06-c4ce-1ecc94114393", "lease_id": "", "renewable": false, "lease_duration": 0, "data": null, "wrap_info": null, "warnings": null, "auth": {"client_token": "s.Token", "accessor": "zFcMdiOHhtXUyRTUigkePpzS", "policies": ["app-aiops", "default"], "token_policies": ["app-aiops", "default"], "metadata": null, "lease_duration": 2764800, "renewable": true, "entity_id": "", "token_type": "service", "orphan": false}}
```

## Building the project
From the root of the project directory, run the build with the following command:

`./mvnw clean install -pl :foundation-encryption-policy-java,:aissemble-foundation-encryption-policy-python,:extensions-encryption-vault-java,:aissemble-extensions-encryption-vault-python`

Unit tests to verify encryption using AES are executed during the build. 

## Running integration tests
Run the integration tests with the following command:

`./mvnw clean install -pl :extensions-encryption-vault-java,:aissemble-extensions-encryption-vault-python -Pintegration-test`

Integration tests to verify encryption using Vault are executed during the build. 
