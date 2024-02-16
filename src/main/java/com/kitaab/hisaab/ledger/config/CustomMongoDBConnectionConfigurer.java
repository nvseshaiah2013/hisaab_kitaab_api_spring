package com.kitaab.hisaab.ledger.config;

import com.kitaab.hisaab.ledger.constants.CustomConfigConstants;
import com.kitaab.hisaab.ledger.util.VaultUtils;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration to fetch the Mongo Connection url from vault
 *
 */
@Configuration
@Slf4j
@Profile("!dev")
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class CustomMongoDBConnectionConfigurer {

    private final ConfigFileReader.ConfigFile customConfig;

    private final ConfigFileReader.ConfigFile secretsConfig;

    private final AuthenticationDetailsProvider provider;

    public CustomMongoDBConnectionConfigurer(@Qualifier("customConfig") ConfigFileReader.ConfigFile customConfig,
                                             @Qualifier("secretsConfig") ConfigFileReader.ConfigFile secretsConfig,
                                             @Qualifier("ociVaultProvider") AuthenticationDetailsProvider provider) {
        this.customConfig = customConfig;
        this.secretsConfig = secretsConfig;
        this.provider = provider;
    }

    @Bean
    @Primary
    public MongoProperties mongoProperties() {
        var mongoProperties = new MongoProperties();
        mongoProperties.setUri(getMongoDBConnectionUri());
        return mongoProperties;
    }

    private String getMongoDBConnectionUri() {
        log.info("Started fetching mongodb connection uri vault secret : {}", CustomConfigConstants.MONGO_DB_URL_SECRET);
        return VaultUtils.getSecretFromVault(secretsConfig.get(CustomConfigConstants.OCI_REGION),
                customConfig.get(CustomConfigConstants.MONGO_DB_URL_SECRET), provider);
    }
}
