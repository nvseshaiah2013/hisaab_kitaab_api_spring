package com.kitaab.hisaab.ledger.dto.request.users;

public record ResetPasswordRequest(String password, String token, String newPassword) {
}
