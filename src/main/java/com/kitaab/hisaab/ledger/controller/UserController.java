package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.constants.ApplicationConstants;
import com.kitaab.hisaab.ledger.dto.request.users.ChangePasswordRequest;
import com.kitaab.hisaab.ledger.dto.request.users.ForgotPasswordRequest;
import com.kitaab.hisaab.ledger.dto.request.users.ResetPasswordRequest;
import com.kitaab.hisaab.ledger.dto.request.users.LoginRequest;
import com.kitaab.hisaab.ledger.dto.request.users.SignupRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstants.USERS_BASE_URL)
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;

    @GetMapping(ApplicationConstants.BASE_URL)
    public ResponseEntity<SuccessResponse> getAllUsers(@RequestParam("username") String username) {
        log.info("Fetching users having prefix with username : {}" , username);
        return ResponseEntity.ok(userService.getUsers(username));
    }

    @PostMapping(ApplicationConstants.LOGIN_ENDPOINT)
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in the user with username : {}", loginRequest.username());
        return ResponseEntity.ok(userService.login(loginRequest.username(), loginRequest.password()));
    }


    @PostMapping(ApplicationConstants.SIGN_UP_ENDPOINT)
    public ResponseEntity<SuccessResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("User registration for user with name : {} and username : {}", signupRequest.name(), signupRequest.username());
        return ResponseEntity.ok(userService.signup(signupRequest.name(), signupRequest.username(), signupRequest.password()));
    }

    @PostMapping(ApplicationConstants.CHANGE_PASSWORD_ENDPOINT)
    public ResponseEntity<SuccessResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User password change requested for : {}", authentication.getPrincipal());
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest.oldPassword(), changePasswordRequest.newPassword()));
    }

    @PostMapping(ApplicationConstants.RESET_PASSWORD)
    public ResponseEntity<SuccessResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Changing the password for user");
        return ResponseEntity.ok(userService.resetPassword(resetPasswordRequest.token(), resetPasswordRequest.newPassword()));
    }

    @PostMapping(ApplicationConstants.REQUEST_RESET_PASSWORD)
    public ResponseEntity<SuccessResponse> requestResetPassword(@RequestParam String username) {
        log.info("User {} has forgotten password. Sending password change token", username);
        return ResponseEntity.ok(userService.requestResetPassword(username));

    }
}