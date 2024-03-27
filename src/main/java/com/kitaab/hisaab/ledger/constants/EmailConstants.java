package com.kitaab.hisaab.ledger.constants;

import jakarta.ws.rs.NotSupportedException;

public class EmailConstants {
    // Reset Email Properties
    public static final String RESET_PASSWORD = "Reset Password";
    public static final String RESET_PASSWORD_FILE = "reset-password";
    public static final String RESET_PASSWORD_EMAIL_SUBJECT = "Reset Your Hisaab Kitaab Account Password";
    private static final String GREETINGS_TEXT = "Dear %s";
    private EmailConstants() {
        throw new NotSupportedException("Unable to create object for this");
    }

    public static String getGreetingsText(String name) {
        return String.format(GREETINGS_TEXT, name);
    }


}
