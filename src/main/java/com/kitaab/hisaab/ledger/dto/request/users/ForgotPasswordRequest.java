package com.kitaab.hisaab.ledger.dto.request.users;

public record ForgotPasswordRequest(String username, String token, String newPassword) {
}
