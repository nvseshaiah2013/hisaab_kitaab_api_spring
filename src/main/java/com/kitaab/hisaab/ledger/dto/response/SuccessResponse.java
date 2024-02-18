package com.kitaab.hisaab.ledger.dto.response;

import com.kitaab.hisaab.ledger.Model.Response;

public record SuccessResponse (org.springframework.http.HttpStatus status, String message, Object payload) implements Response {
}