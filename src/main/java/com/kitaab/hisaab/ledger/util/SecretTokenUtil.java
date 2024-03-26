package com.kitaab.hisaab.ledger.util;

import jakarta.ws.rs.NotSupportedException;
import org.apache.commons.lang3.RandomStringUtils;

public class SecretTokenUtil {

    private static final Integer TOKEN_LENGTH = 9;

    private static final Boolean USE_LETTERS = true;

    private static final Boolean USE_NUMBERS = true;

    public SecretTokenUtil() {
        throw new NotSupportedException("Creating a instance of this class is not supported");
    }

    public static String generateRandomToken() {
        return RandomStringUtils.random(TOKEN_LENGTH, USE_LETTERS, USE_NUMBERS);
    }
}
