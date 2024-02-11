package com.kitaab.hisaab.ledger.service;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;

public interface UserService {

    SuccessResponse login(String username, String password);

    SuccessResponse signup(String name, String username, String password);

    SuccessResponse changePassword(String oldPassword, String newPassword);

    SuccessResponse requestForgotPassword(String username);

    SuccessResponse forgotPassword(String authToken, String newPassword);

    SuccessResponse getUsers(String usernamePrefix);

}