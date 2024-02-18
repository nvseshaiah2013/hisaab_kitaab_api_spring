package com.kitaab.hisaab.ledger.dto.response;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

public record SuccessResponse (HttpStatusCode status, String message, Map<String, Object> payload) {

    public SuccessResponse(HttpStatusCode status, String message) {
        this(status, message, new HashMap<>());
    }

    public Map<String, Object> put(String key, Object value) {
        payload.put(key, value);
        return this.payload;
    }
}