package com.kitaab.hisaab.ledger.constants;

import jakarta.ws.rs.NotSupportedException;

public class ApplicationConstants {

    public ApplicationConstants() {
        throw new NotSupportedException("Cannot instantiate this class");
    }

    public static final String[] ALLOW_USERS_ENDPOINT = {
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui/**",
            "/actuator/**", "/api/users/**"
            // other public endpoints of your API may be appended to this array
    };

    public static final String CHANGE_PASSWORD_ENDPOINT = "/api/users/changePassword";

    public static final String AUTHORIZATION_ERROR = "Authorization Error";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String ACTUATOR_ENDPOINT = "/actuator/**";
}
