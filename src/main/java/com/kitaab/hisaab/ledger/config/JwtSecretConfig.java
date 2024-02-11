package com.kitaab.hisaab.ledger.config;

import com.kitaab.hisaab.ledger.constants.CustomConfigConstants;
import com.kitaab.hisaab.ledger.util.VaultUtils;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kitaab.hisaab.ledger.constants.CustomConfigConstants.JWT_SIGNING_KEY;

@Configuration
@Slf4j
public class JwtSecretConfig {

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
    public JwtSecret jwtSecret() {
        return new JwtSecret(fetchJwtSecret());
    }

    private String fetchJwtSecret() {
        log.info("Fetching Jwt Secret Signing Key");
        return VaultUtils.getSecretFromVault(secretsConfig.get(CustomConfigConstants.OCI_REGION),
                customConfig.get(JWT_SIGNING_KEY), provider);
    }
}
