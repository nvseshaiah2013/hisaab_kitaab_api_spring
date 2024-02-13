package com.kitaab.hisaab.ledger.dto.response;

public record SuccessResponse (boolean status, String message, Object payload) implements Response {
    @Override
    public boolean getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}