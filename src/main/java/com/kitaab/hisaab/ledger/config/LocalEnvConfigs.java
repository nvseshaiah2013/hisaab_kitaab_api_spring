package com.kitaab.hisaab.ledger.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class LocalEnvConfigs {
    @Bean
    public JwtSecret jwtSecret(@Value("${application.jwt-key}") String signKey) {
        return new JwtSecret(signKey);
    }

    @Bean
    @Primary
    public MongoProperties mongoProperties(@Value("${application.mongodb-url}") String connectionString) {
        var mongoProperties = new MongoProperties();
        mongoProperties.setUri(connectionString);
        return mongoProperties;
    }
}
