package com.kitaab.hisaab.ledger.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.text.MessageFormat;

@Getter
public enum ExceptionEnum {

    CHANGE_PASSWORD_EXCEPTION("The old password provided does not match with existing password", "EX-0001"),
    UNABLE_TO_SAVE_DATA("Unable to save the data to DB", "EX-0002"),
    UNABLE_TO_SEND_EMAIL("Unable to send email", "EX-0003"),
    USERNAME_NOT_FOUND("Unable to find user with username {0}", "EX-0004"),
    DUPLICATE_USER_EXCEPTION("User with Username: {} already Exist in Database", "EX-0005", HttpStatus.CONFLICT),
    PASSWORD_RESET_TOKEN_INVALID("The supplied password reset token is invalid or expired", "EX-0006", HttpStatus.UNAUTHORIZED),
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