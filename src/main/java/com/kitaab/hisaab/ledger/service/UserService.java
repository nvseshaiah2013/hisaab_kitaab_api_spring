package com.kitaab.hisaab.ledger.service;

import com.kitaab.hisaab.ledger.dto.response.Response;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;

public interface UserService {

    SuccessResponse login(String username, String password);

    Response signup(String name, String username, String password);

    SuccessResponse changePassword(String oldPassword, String newPassword);

    SuccessResponse requestResetPassword(String username);

    SuccessResponse resetPassword(String authToken, String newPassword);

    SuccessResponse getUsers(String usernamePrefix);

}