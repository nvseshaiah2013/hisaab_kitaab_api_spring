package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.JwtService;
import com.kitaab.hisaab.ledger.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    @Override
    public SuccessResponse login(String username, String password) {
        var authentication = Optional
                .ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .orElseThrow(() -> new BadCredentialsException(username));
        var userDetails = (UserDetails) authentication.getPrincipal();
        var jwt = jwtService.generateToken(userDetails);
        return new SuccessResponse(true, "You are authenticated!", jwt);
    }

    @Override
    public SuccessResponse signup(String name, String username, String password) {
        return null;
    }

    @Override
    public SuccessResponse changePassword(String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public SuccessResponse requestForgotPassword(String username) {
        return null;
    }

    @Override
    public SuccessResponse forgotPassword(String authToken, String newPassword) {
        return null;
    }

    @Override
    public SuccessResponse getUsers(String usernamePrefix) {
        return null;
    }
}