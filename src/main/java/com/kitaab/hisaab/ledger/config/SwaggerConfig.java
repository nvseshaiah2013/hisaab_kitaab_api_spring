package com.kitaab.hisaab.ledger.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi() {
        ArrayList<Server> servers = new ArrayList<>();
        servers.add(new Server().url("/ledger/").description("Your Expense Buddy"));
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")))
                .servers(servers)
                .info(getApiInfo())
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt", Arrays.asList("read", "write"))
                        .addList("bearer-key", Collections.emptyList()));

    }

    private Info getApiInfo() {
        return new Info()
                .title("Ledger Service")
                .description("Documentation of API v.1.0")
                .version("1.0")
                .license(license())
                .contact(contact());
    }

    private License license() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/mit-license.php");
    }

    private Contact contact() {
        return new Contact()
                .name("Ledger")
                .url("https://hisabkitab.rahulojha.in")
                .email("help@ledger.com");
    }
}
