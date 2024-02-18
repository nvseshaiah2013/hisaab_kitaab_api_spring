package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.ApplicationConstants;
import com.kitaab.hisaab.ledger.constants.EmailTypeEnum;
import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.entity.user.EmailDetails;
import com.kitaab.hisaab.ledger.entity.user.TokenMetadata;
import com.kitaab.hisaab.ledger.entity.user.User;
import com.kitaab.hisaab.ledger.exception.FlowBreakerException;
import com.kitaab.hisaab.ledger.repository.TokenMetadataRepository;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.EmailService;
import com.kitaab.hisaab.ledger.service.JwtService;
import com.kitaab.hisaab.ledger.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @NonNull
    private EmailService emailService;

    @NonNull
    private TokenMetadataRepository tokenMetadataRepository;

    @Value("${application.base-url}")
    private String baseUrl;

    @NonNull
    private AuthenticationManager authenticationManager;

    @NonNull
    private JwtService jwtService;

    @NonNull
    private UserRepository userRepository;

    @NonNull
    private PasswordEncoder encoder;

    @Override
    public SuccessResponse login(String username, String password) {
        log.info("Authenticating the user {} with supplied credentials", username);
        var authentication = Optional
                .ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .orElseThrow(() -> new BadCredentialsException(username));

        log.debug("Password authentication token generated successfully for the user : {}", username);
        var userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.debug("Started generating the JWT Token for the user: {}", username);
        var jwt = jwtService.generateToken(userDetails);
        log.debug("JWT successfully generated for the user : {}", username);

        var response = new SuccessResponse(HttpStatus.OK, ApplicationConstants.AUTHENTICATION_SUCCESS);
        response.put("token", jwt)
                .put("name", userDetails.get("name"));
        return response;
    }

    @Override
    public SuccessResponse signup(String name, String username, String password) {
        log.debug("Trying to signup user with username: {}", username);
        if (userRepository.existsByUsername(username)) {
            log.error(ExceptionEnum.DUPLICATE_USER_EXCEPTION.getMessage(), username);
            throw new FlowBreakerException(
                    ExceptionEnum.DUPLICATE_USER_EXCEPTION
                    .getFormattedMessage(username), ExceptionEnum.DUPLICATE_USER_EXCEPTION);
        }

        log.debug("Mapping the received user credentials to database document");
        User newUser = User.builder()
                .withName(name)
                .withUsername(username)
                .withPassword(encoder.encode(password))
                .build();

        log.debug("Saving the mapped user document to the database");
        userRepository.save(newUser);
        log.info("User signup successful for username: {}", username);

        return new SuccessResponse(HttpStatus.CREATED,
                MessageFormat.format(ApplicationConstants.NEW_USER_CREATION_SUCCESS, username));
    }

    @Override
    public SuccessResponse changePassword(String oldPassword, String newPassword) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Trying to change the password for the user : {}", userDetails.getUsername());
        return Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()))
                .map(user -> {
                    if (encoder.matches(oldPassword, user.getPassword())) {
                        log.debug("Existing password of the user matched for username : {}", user.getUsername());
                        user.setPassword(encoder.encode(newPassword));
                        userRepository.save(user);
                        log.debug("Password changed for the user : {}", user.getUsername());
                        return new SuccessResponse(HttpStatus.OK, ApplicationConstants.PASSWORD_CHANGE_SUCCESS);
                    } else {
                        log.error(ExceptionEnum.CHANGE_PASSWORD_EXCEPTION.getMessage());
                        throw new FlowBreakerException(ExceptionEnum.CHANGE_PASSWORD_EXCEPTION.getMessage(),
                                ExceptionEnum.CHANGE_PASSWORD_EXCEPTION);
                    }
                })
                .orElseThrow(() -> new IllegalStateException(ExceptionEnum.UNEXPECTED_EXCEPTION.getFormattedMessage()));
    }

    @Override
    public SuccessResponse requestResetPassword(String username) {
        log.debug("Reset password requested for user : {}", username);
        var user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getFormattedMessage(username),
                        ExceptionEnum.CHANGE_PASSWORD_EXCEPTION));

        log.debug("Generating password resetting token for username: {}", username);
        String token = jwtService.generateToken(username);
        log.debug("Password reset token generated for user: {}", username);

        EmailDetails emailDetails = EmailDetails.builder()
                .withTo(username)
                .withName(user.getName())
                .withToken(token)
                .withBaseUrl(baseUrl)
                .build();

        log.debug("Built emailDetails for user: {}", username);
        TokenMetadata metadata = TokenMetadata.builder()
                .withToken(token)
                .withUsername(username)
                .build();

        log.debug("Built reset token document for user: {}", username);
        metadata = Optional.of(tokenMetadataRepository.save(metadata))
                .orElseThrow(() -> new FlowBreakerException(
                        ExceptionEnum.UNABLE_TO_SAVE_DATA.getMessage(),
                        ExceptionEnum.UNABLE_TO_SAVE_DATA
                ));

        log.debug("Saved token details for user: {} with document id : {}", username, metadata.getId());

        emailService.sendEmail(emailDetails, EmailTypeEnum.RESET_PASSWORD)
                .whenComplete((re, exc) -> {
                    if (exc == null) {
                        log.info("Email is sent to user");
                    }
                    throw new FlowBreakerException(ExceptionEnum.UNABLE_TO_SEND_EMAIL.getMessage(),
                            ExceptionEnum.UNABLE_TO_SEND_EMAIL);
                });
        return new SuccessResponse(HttpStatus.CREATED, ApplicationConstants.PASSWORD_RESET_MAIL_SENT);
    }

    @Override
    public SuccessResponse resetPassword(String authToken, String newPassword) {
        String username = jwtService.extractUsername(authToken);
        log.info("Trying to change password for user: {}", username);
        var tokenMetadata = Optional.ofNullable(tokenMetadataRepository.findByUsernameOrderByCreatedAtDesc(username))
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getFormattedMessage(username),
                        ExceptionEnum.UNABLE_TO_SAVE_DATA));

        log.debug("Validating password reset token");
        Boolean isValid = jwtService.validateToken(tokenMetadata.getToken(), username);
        if (isValid && Objects.equals(tokenMetadata.getToken(), authToken)) {
            log.debug("Password reset token is valid for user:{}", username);
            User user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(
                    () -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getFormattedMessage(username),
                            ExceptionEnum.USERNAME_NOT_FOUND)
            );
            user.setPassword(encoder.encode(newPassword));
            user = Optional.of(userRepository.save(user)).orElseThrow(
                    () -> new FlowBreakerException(ExceptionEnum.UNABLE_TO_SAVE_DATA.getFormattedMessage(),
                            ExceptionEnum.UNABLE_TO_SAVE_DATA));
            log.debug("Password is reset for the user: {}", user.getUsername());
        } else {
            log.error(ExceptionEnum.PASSWORD_RESET_TOKEN_INVALID.getMessage());
            throw new FlowBreakerException(ExceptionEnum.PASSWORD_RESET_TOKEN_INVALID.getMessage(),
                    ExceptionEnum.PASSWORD_RESET_TOKEN_INVALID);
        }
        return new SuccessResponse(HttpStatus.OK,
                MessageFormat.format(ApplicationConstants.PASSWORD_RESET_SUCCESS, username));
    }

    @Override
    public SuccessResponse getUsers(String usernamePrefix) {
        log.info("Finding all Users starting with Username : {}", usernamePrefix);
        List<User> users = userRepository.findByUsernameStartsWith(usernamePrefix);
        log.info("Found {} users starting with username : {}", users.size(), usernamePrefix);
        var response = new SuccessResponse(HttpStatus.OK, MessageFormat.format("Found {0} users", users.size()));
        response.put("users", users);
        return response;
    }
}
