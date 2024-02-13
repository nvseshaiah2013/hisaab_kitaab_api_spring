package com.kitaab.hisaab.ledger.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    CHANGE_PASSWORD_EXCEPTION("The old password provided does not match with existing password", "EX-0001"),
    UNEXPECTED_EXCEPTION("Some unexpected exception occurred", "EX-9999");

    private final String message;
    private final String errorCode;


}