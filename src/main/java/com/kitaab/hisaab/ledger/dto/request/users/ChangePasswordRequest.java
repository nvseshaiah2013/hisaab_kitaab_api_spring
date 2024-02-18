package com.kitaab.hisaab.ledger.dto.request.users;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
