package com.kitaab.hisaab.ledger.exception;

import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class FlowBreakerException extends RuntimeException{

    private final String errorCode;
    private final HttpStatusCode statusCode;

    public FlowBreakerException(String message, ExceptionEnum exceptionEnum) {
        super(message);
        this.errorCode = exceptionEnum.getErrorCode();
        this.statusCode = exceptionEnum.getStatusCode();
    }
}
