package com.kitaab.hisaab.ledger.config;


import com.kitaab.hisaab.ledger.constants.ApplicationConstants;
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
        servers.add(new Server().url(ApplicationConstants.SERVER_URL).description(ApplicationConstants.SERVER_DESCRIPTION));
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(ApplicationConstants.BEARER_KEY,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme(ApplicationConstants.SECURITY_SCHEME).bearerFormat(ApplicationConstants.SECURITY_BEARER_FORMAT)))
                .servers(servers)
                .info(getApiInfo())
                .addSecurityItem(new SecurityRequirement()
                        .addList(ApplicationConstants.BEARER_JWT, Arrays.asList("read", "write"))
                        .addList(ApplicationConstants.BEARER_KEY, Collections.emptyList()));

    }

    private Info getApiInfo() {
        return new Info()
                .title(ApplicationConstants.API_TITLE)
                .description(ApplicationConstants.API_DESCRIPTION)
                .version(ApplicationConstants.API_VERSION)
                .license(license())
                .contact(contact());
    }

    private License license() {
        return new License()
                .name(ApplicationConstants.LICENCE_NAME)
                .url(ApplicationConstants.LICENCE_URL);
    }

    private Contact contact() {
        return new Contact()
                .name(ApplicationConstants.CONTACT_NAME)
                .url(ApplicationConstants.CONTACT_URL)
                .email(ApplicationConstants.CONTACT_EMAIL);
    }
}
