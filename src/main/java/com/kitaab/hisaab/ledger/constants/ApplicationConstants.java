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

    public static final String AUTHORIZATION_ERROR = "Authorization Error";
    public static final String DUPLICATE_USER_ERROR = "Duplicate User Error";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACTUATOR_ENDPOINT = "/actuator/**";
    public static final String BEARER_KEY = "bearer-key";
    public static final String BEARER_JWT = "bearer-jwt";
    public static final String LICENCE_NAME = "MIT License";
    public static final String LICENCE_URL = "https://opensource.org/licenses/mit-license.php";
    public static final String CONTACT_NAME = "Ledger";
    public static final String CONTACT_EMAIL = "help@ledger.com";
    public static final String CONTACT_URL= "https://hisabkitab.rahulojha.in";
    public static final String SECURITY_BEARER_FORMAT= "JWT";
    public static final String SECURITY_SCHEME= "bearer";
    public static final String API_TITLE= "Ledger Service";
    public static final String API_DESCRIPTION= "Documentation of API v.1.0";
    public static final String API_VERSION = "1.0";
    public static final String SERVER_URL = "/ledger/";
    public static final String SERVER_DESCRIPTION = "Your Expense Buddy";


    public static final String BASE_URL = "/";

    public static final String USERS_BASE_URL = "/api/users";

    public static final String LOGIN_ENDPOINT = "/login";

    public static final String SIGN_UP_ENDPOINT = "/signup";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    public static final String FORGOT_PASSWORD = "/forgotPassword";
}
