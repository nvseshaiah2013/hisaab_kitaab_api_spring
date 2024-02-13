package com.kitaab.hisaab.ledger.dto.response;

public record ErrorMessage (String header, String message) implements  Response  {
    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Object getPayload() {
        return null;
    }
}