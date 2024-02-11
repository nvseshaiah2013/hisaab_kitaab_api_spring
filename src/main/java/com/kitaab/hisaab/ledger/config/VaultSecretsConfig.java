package com.kitaab.hisaab.ledger.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class VaultSecretsConfig {

    @Value("${config.customConfigFilePath}")
    private String customConfigFilePath;

    @Value("${config.credentialConfigFilePath}")
    private String configFilePath;

    @Bean
    @Qualifier("customConfig")
    public ConfigFileReader.ConfigFile customConfig() throws IOException {
        log.debug("Reading custom config from : {}", customConfigFilePath);
        return ConfigFileReader.parse(customConfigFilePath);
    }

    @Bean
    @Qualifier("secretsConfig")
    public ConfigFileReader.ConfigFile secretsConfig() throws IOException {
        log.debug("Reading secrets config from : {}", configFilePath);
        return ConfigFileReader.parse(configFilePath);
    }

    @Bean
    @Qualifier("ociVaultProvider")
    public AuthenticationDetailsProvider authenticationDetailsProvider() throws IOException {
        return new ConfigFileAuthenticationDetailsProvider(secretsConfig());
    }
}