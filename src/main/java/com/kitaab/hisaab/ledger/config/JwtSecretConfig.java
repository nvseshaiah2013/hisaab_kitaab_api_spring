package com.kitaab.hisaab.ledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtSecretConfig {

    @Bean
    public JwtSecret jwtSecret() {
        var jwtSecret = new JwtSecret();
        return jwtSecret;
    }
}
