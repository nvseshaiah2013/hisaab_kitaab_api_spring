package com.kitaab.hisaab.ledger.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Configuration
@Slf4j
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class CustomMongoDBConnectionConfigurer {

    @Value("${config.credentialConfigFilePath}")
    private String configFilePath;

    @Bean
    @Primary
    public MongoProperties mongoProperties() {
        var mongoProperties = new MongoProperties();
        mongoProperties.setUri(getMongoDBConnectionUri());
        return mongoProperties;
    }

    public String getMongoDBConnectionUri() {
        try {
            ConfigFileReader.ConfigFile config = getConfigFile();
            Supplier<InputStream> privateKeySupplier
                    = new SimplePrivateKeySupplier(config.get("key_file"));
            AuthenticationDetailsProvider provider = getAuthenticationDetailsProvider(config, privateKeySupplier);
            return getSecretFromVault(config.get("region"), config.get("vault-ocid"), provider);
        } catch (IOException e) {
            log.error("Exception occurred while fetching MongoDB DB Connection Uri");
            throw new RuntimeException(e);
        }
    }

    private AuthenticationDetailsProvider getAuthenticationDetailsProvider(ConfigFileReader.ConfigFile config, Supplier<InputStream> privateKey) {
        log.info("Started building Authentication Details Provider");
        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(config.get("tenancy"))
                .userId(config.get("user"))
                .fingerprint(config.get("fingerprint"))
                .region(Region.valueOf(config.get("region")))
                .privateKeySupplier(privateKey)
                .passPhrase(config.get("passphrase"))
                .build();
    }

    private ConfigFileReader.ConfigFile getConfigFile() throws IOException {
        log.info("Parsing config file from the path: {}", configFilePath);
        return ConfigFileReader.parse(configFilePath);
    }


    private String getSecretFromVault(String region, String secretOcid, AuthenticationDetailsProvider provider) {
        log.info("Fetching the vault secret from the region : {}", region);
        GetSecretBundleResponse getSecretBundleResponse;
        try (SecretsClient secretsClient = SecretsClient.builder().build(provider)) {
            secretsClient.setRegion(region);
            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest
                    .builder()
                    .secretId(secretOcid)
                    .stage(GetSecretBundleRequest.Stage.Current)
                    .build();
            getSecretBundleResponse = secretsClient.getSecretBundle(getSecretBundleRequest);
        }
        log.debug("Started decoding the base64 secret bundle");
        Base64SecretBundleContentDetails base64SecretBundleContentDetails =
                (Base64SecretBundleContentDetails) getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
        byte[] secretValueDecoded = Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
        log.debug("Finished decoding the base64 secret bundle");
        return new String(secretValueDecoded);
    }

}
