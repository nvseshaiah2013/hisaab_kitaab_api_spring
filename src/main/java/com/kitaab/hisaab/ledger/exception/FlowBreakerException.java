package com.kitaab.hisaab.ledger.exception;

import lombok.Getter;

@Getter
public class FlowBreakerException extends RuntimeException{

    private final String errorCode;

    public FlowBreakerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
