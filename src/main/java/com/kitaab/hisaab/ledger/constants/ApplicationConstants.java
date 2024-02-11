package com.kitaab.hisaab.ledger.constants;

import jakarta.ws.rs.NotSupportedException;

public class ApplicationConstants {

    public ApplicationConstants() {
        throw new NotSupportedException("Cannot instantiate this class");
    }

    public static final String ALLOW_USERS_ENDPOINT = "/api/users/**";

    public static final String CHANGE_PASSWORD_ENDPOINT = "/api/users/changePassword";

    public static final String AUTHORIZATION_ERROR = "Authorization Error";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer ";
}
