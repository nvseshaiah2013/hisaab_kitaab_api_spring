package com.kitaab.hisaab.ledger.config;

import com.kitaab.hisaab.ledger.constants.CustomConfigConstants;
import com.kitaab.hisaab.ledger.util.VaultUtils;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * Configuration to fetch the Mongo Connection url from vault
 *
 */
@Configuration
@Slf4j
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class CustomMongoDBConnectionConfigurer {

    @Value("${config.credentialConfigFilePath}")
    private String configFilePath;

    @Value("${config.customConfigFilePath}")
    private String customConfigFilePath;

    @Bean
    @Primary
    public MongoProperties mongoProperties() {
        var mongoProperties = new MongoProperties();
        mongoProperties.setUri(getMongoDBConnectionUri());
        return mongoProperties;
    }

    private String getMongoDBConnectionUri() {
        log.info("Started fetching mongodb connection uri");
        try {
            log.debug("Reading secrets config from : {}", configFilePath);
            final var secretsConfig = ConfigFileReader.parse(configFilePath);
            log.debug("Reading custom config from : {}", customConfigFilePath);
            final var customConfig = ConfigFileReader.parse(customConfigFilePath);
            final var provider = new ConfigFileAuthenticationDetailsProvider(secretsConfig);
            log.debug("Fetching vault secret {}", CustomConfigConstants.MONGO_DB_URL_SECRET);
            return VaultUtils.getSecretFromVault(secretsConfig.get(CustomConfigConstants.OCI_REGION),
                    customConfig.get(CustomConfigConstants.MONGO_DB_URL_SECRET), provider);
        } catch (IOException e) {
            log.error("Exception occurred while fetching Mongo DB Connection Uri");
            throw new RuntimeException(e);
        }
    }
}
