package com.kitaab.hisaab.ledger.dto.response;

public record SuccessResponse (boolean status, String message, Object payload) {
}