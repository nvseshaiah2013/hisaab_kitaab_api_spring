package com.kitaab.hisaab.ledger.constants;

import jakarta.ws.rs.NotSupportedException;

public class ApplicationConstants {

    public ApplicationConstants() {
        throw new NotSupportedException("Cannot instantiate this class");
    }

    public static final String ALLOW_USERS_ENDPOINT = "/api/users/**";

    public static final String AUTHORIZATION_ERROR = "Authorization Error";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String ACTUATOR_ENDPOINT = "/actuator/**";

    public static final String BASE_URL = "/";

    public static final String USERS_BASE_URL = "/api/users";

    public static final String LOGIN_ENDPOINT = "/login";

    public static final String SIGN_UP_ENDPOINT = "signup";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    public static final String FORGOT_PASSWORD = "/forgotPassword";
}
