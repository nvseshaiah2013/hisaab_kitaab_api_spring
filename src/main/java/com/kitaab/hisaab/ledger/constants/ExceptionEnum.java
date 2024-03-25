package com.kitaab.hisaab.ledger.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.text.MessageFormat;

@Getter
public enum ExceptionEnum {

    CHANGE_PASSWORD_EXCEPTION("The old password provided does not match with existing password",
            "EX-0001", HttpStatus.UNAUTHORIZED),
    UNABLE_TO_SAVE_DATA("Unable to save the data to DB", "EX-0002"),
    UNABLE_TO_SEND_EMAIL("Unable to send email", "EX-0003"),
    USERNAME_NOT_FOUND("Unable to find user with username {0}", "EX-0004",
            HttpStatus.NOT_FOUND),
    DUPLICATE_USER_EXCEPTION("User with Username: {0} already Exist in Database",
            "EX-0005", HttpStatus.CONFLICT),
    PASSWORD_RESET_TOKEN_INVALID("The supplied password reset token is invalid or expired",
            "EX-0006", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("The supplied credentials do not match our records", "EX-0007",
            HttpStatus.UNAUTHORIZED),
    NO_BORROW_RECORD_FOUND("No borrow record found for the borrow id: {0}", "EX-0008",
            HttpStatus.NOT_FOUND),
    BORROW_RECORD_CANNOT_BE_UPDATED("Borrow record {0} cannot be updated", "EX-0009",
            HttpStatus.FORBIDDEN),
    USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD("Borrow record {0} cannot be {1} as it is owned by {2}",
            "EX-0010",HttpStatus.FORBIDDEN),
    BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION("Borrow record {0} is in inconsistent state for requested action {1}",
            "EX-0011",HttpStatus.FORBIDDEN),
    BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND("Borrow token expired or the given token not found. Please " +
            "try resending the token.", "EX-0012",
            HttpStatus.UNAUTHORIZED),
    BORROW_TOKEN_INVALID("Provided secret token is invalid. Please try again with valid secret code.", "EX-0013", HttpStatus.UNAUTHORIZED),
    UNEXPECTED_EXCEPTION("Some unexpected exception occurred", "EX-9999");

    private final String message;
    private final String errorCode;
    private final HttpStatusCode statusCode;

    ExceptionEnum(String message, String errorCode, HttpStatusCode statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    ExceptionEnum(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public String getFormattedMessage(String... args) {
        return MessageFormat.format(this.message, (Object[]) args);
    }

}