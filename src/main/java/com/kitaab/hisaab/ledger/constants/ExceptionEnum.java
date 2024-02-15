package com.kitaab.hisaab.ledger.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    CHANGE_PASSWORD_EXCEPTION("The old password provided does not match with existing password", "EX-0001"),
    UNABLE_TO_SAVE_DATA("Unable to save the data to DB", "EX-0002"),
    UNABLE_TO_SEND_EMAIL("Unable to send email", "EX-0003"),
    USERNAME_NOT_FOUND("Unable to find user with username {0}", "EX-0004"),
    UNEXPECTED_EXCEPTION("Some unexpected exception occurred", "EX-9999");

    private final String message;
    private final String errorCode;

    public String getMessage(String... args) {
        return MessageFormat.format(this.message, (Object[]) args);
    }


}