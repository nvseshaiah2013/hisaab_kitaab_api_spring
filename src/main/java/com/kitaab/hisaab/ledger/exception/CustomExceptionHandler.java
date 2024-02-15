package com.kitaab.hisaab.ledger.exception;

import com.kitaab.hisaab.ledger.constants.ApplicationConstants;

import com.kitaab.hisaab.ledger.dto.response.ErrorResponse;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Objects;

@RestControllerAdvice
public class CustomExceptionHandler {

    @Autowired
    Tracer tracer;

    String traceId;
    String spanId;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentException(MethodArgumentNotValidException ex) {
        return getErrorResponseResponseEntity(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return getErrorResponseResponseEntity(ex);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerUsernameNotFoundException(UsernameNotFoundException ex) {
        traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        spanId = Objects.requireNonNull(tracer.currentSpan()).context().spanId();
        ErrorResponse message = new ErrorResponse(new Date(), ex.getMessage(), ApplicationConstants.USERNAME_NOT_FOUND,
                traceId, spanId);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerGenericException(Exception ex) {
        traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        spanId = Objects.requireNonNull(tracer.currentSpan()).context().spanId();
        ErrorResponse message = new ErrorResponse(new Date(), ex.getMessage(), ApplicationConstants.DEFAULT_ERROR_CODE,
                traceId, spanId);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(Exception ex) {
        traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        spanId = Objects.requireNonNull(tracer.currentSpan()).context().spanId();
        ErrorResponse message = new ErrorResponse(new Date(), ex.getMessage(), ApplicationConstants.DEFAULT_ERROR_CODE,
                traceId, spanId);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}