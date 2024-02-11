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

/**
 * Configuration to fetch the Mongo Connection url from vault
 *
 */
@Configuration
@Slf4j
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class CustomMongoDBConnectionConfigurer {

    @Autowired
    @Qualifier("customConfig")
    private ConfigFileReader.ConfigFile customConfig;

    @Autowired
    @Qualifier("secretsConfig")
    private ConfigFileReader.ConfigFile secretsConfig;

    @Autowired
    @Qualifier("ociVaultProvider")
    private AuthenticationDetailsProvider provider;

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