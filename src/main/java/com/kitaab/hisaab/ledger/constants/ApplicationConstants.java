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
    public static final String NO_SUCH_USER = "No Such User";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACTUATOR_ENDPOINT = "/actuator/**";
    public static final String BEARER_KEY = "bearer-key";
    public static final String BEARER_JWT = "bearer-jwt";
    public static final Long DEFAULT_JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 24L;
    public static final Long RESET_PASSWORD_TOKEN_VALIDITY = 1000 * 60 * 10L;
    public static final String LICENCE_NAME = "MIT License";
    public static final String LICENCE_URL = "https://opensource.org/licenses/mit-license.php";
    public static final String CONTACT_NAME = "Ledger";
    public static final String CONTACT_EMAIL = "help@ledger.com";
    public static final String CONTACT_URL = "https://hisabkitab.rahulojha.in";
    public static final String SECURITY_BEARER_FORMAT = "JWT";
    public static final String SECURITY_SCHEME = "bearer";
    public static final String API_TITLE = "Ledger Service";
    public static final String API_DESCRIPTION = "Documentation of API v.1.0";
    public static final String API_VERSION = "1.0";
    public static final String SERVER_URL = "/ledger/";
    public static final String SERVER_DESCRIPTION = "Your Expense Buddy";
    public static final String BASE_URL = "/";
    public static final String USERS_BASE_URL = "/api/users";
    public static final String TOKEN_BASE_URL = "/api/token";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String SIGN_UP_ENDPOINT = "/signup";
    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";
    public static final String RESET_PASSWORD = "/resetPassword";
    public static final String REQUEST_RESET_PASSWORD = "/requestResetPassword";

    public static final String BORROWS_BASE_URL = "/api/borrow";
    public static final String BORROW_ITEMS = "/borrowItem";
    public static final String BORROW_ITEMS_BY_ID = BORROW_ITEMS + "/{borrowId}";
    public static final String RETURN_BORROW_ITEM_BY_ID = BORROW_ITEMS_BY_ID + "/return";
    public static final String TAKEN_ITEMS = "/takenItems";
    public static final String VALIDATE_BORROW = "/validateborrow/{borrowId}";
    public static final String VALIDATE_RETURN = "/validatereturn/{borrowId}";
    public static final String REJECT_BORROW = "/reject/{borrowId}";
    public static final String DEFAULT_ERROR_CODE = "LEDGER-400-VE-001";
    public static final String USERNAME_NOT_FOUND = "LEDGER-400-VE-004";
    public static final String AUTHENTICATION_SUCCESS = "You are authenticated!";
    public static final String NEW_USER_CREATION_SUCCESS = "New user created with username: {0}";
    public static final String PASSWORD_CHANGE_SUCCESS = "Changed Password";
    public static final String PASSWORD_RESET_MAIL_SENT = "Password reset mail sent successfully";
    public static final String PASSWORD_RESET_SUCCESS = "Password Change Successfully for username: {0}";

    public static final String REJECT_ACTION = "REJECT";
    public static final String VALIDATE_BORROW_ACTION = "VALIDATE_BORROW";
    public static final String VALIDATE_RETURN_ACTION = "VALIDATE_RETURN";
    public static final String RETURN_ACTION = "RETURN";
    public static final String DELETE_ACTION = "DELETE";
    public static final String GET_TOKEN = "{borrowId}/getToken";
    public static final String GENERATE_TOKEN = "{borrowId}/generateToken";
}
