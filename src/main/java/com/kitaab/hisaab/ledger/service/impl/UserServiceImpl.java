package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.EmailTypeEnum;
import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.response.ErrorMessage;
import com.kitaab.hisaab.ledger.dto.response.Response;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
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

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.DUPLICATE_USER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @NonNull
    EmailService emailService;
    @NonNull
    TokenMetadataRepository tokenMetadataRepository;
    @Value("${application.base-url}")
    String baseUrl;
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
        var authentication = Optional
                .ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .orElseThrow(() -> new BadCredentialsException(username));
        var userDetails = (UserDetails) authentication.getPrincipal();
        var jwt = jwtService.generateToken(userDetails);
        return new SuccessResponse(true, "You are authenticated!", jwt);
    }

    @Override
    public Response signup(String name, String username, String password) {
        log.debug("Trying to signup user with username: {}", username);
        if (userRepository.existsByUsername(username)) {
            log.error("User with Username: {} already Exist in Database", username);
            return new ErrorMessage(DUPLICATE_USER_ERROR,
                    MessageFormat.format("User Already Exist With Username {0}", username));
        }

        User newUser = User.builder()
                .withName(name)
                .withUsername(username)
                .withPassword(encoder.encode(password))
                .build();

        newUser = userRepository.save(newUser);
        log.info("User SignUp Successful for Username: {}", username);
        return new SuccessResponse(true,
                MessageFormat.format("New User Created with Username: {0}", username), newUser);
    }

    @Override
    public SuccessResponse changePassword(String oldPassword, String newPassword) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Trying to change the password for the user : {}", userDetails.getUsername());
        return Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()))
                .map(user -> {
                    if (encoder.matches(oldPassword, user.getPassword())) {
                        user.setPassword(encoder.encode(newPassword));
                        userRepository.save(user);
                        return new SuccessResponse(true, "Changed Password", user);
                    } else {
                        log.error(ExceptionEnum.CHANGE_PASSWORD_EXCEPTION.getMessage());
                        throw new FlowBreakerException(ExceptionEnum.CHANGE_PASSWORD_EXCEPTION.getErrorCode(),
                                ExceptionEnum.CHANGE_PASSWORD_EXCEPTION.getMessage());
                    }
                })
                .orElseThrow(() -> new IllegalStateException(ExceptionEnum.UNEXPECTED_EXCEPTION.getMessage()));
    }

    @Override
    public SuccessResponse requestResetPassword(String username) {
        var user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getErrorCode(),
                        ExceptionEnum.USERNAME_NOT_FOUND.getMessage(username)));
        String token = jwtService.generateToken(username);
        EmailDetails emailDetails = EmailDetails.builder()
                .withTo(username)
                .withName(user.getName())
                .withToken(token)
                .withBaseUrl(baseUrl)
                .build();
        TokenMetadata metadata = TokenMetadata.builder()
                .withToken(token)
                .withUsername(username)
                .build();
        metadata = Optional.of(tokenMetadataRepository.save(metadata))
                .orElseThrow(() -> new FlowBreakerException(
                        ExceptionEnum.UNABLE_TO_SAVE_DATA.getErrorCode(),
                        "Unable to save data to DB"
                ));
        emailService.sendEmail(emailDetails, EmailTypeEnum.RESET_PASSWORD).whenComplete(
                (re, exc) -> {
                    if (exc == null) {
                        log.info("Email is sent to user");
                    }
                    throw new FlowBreakerException(ExceptionEnum.UNABLE_TO_SEND_EMAIL.getErrorCode(),
                            "Unable to send the email");
                }
        );
        return new SuccessResponse(true, "Mail Sent", emailDetails);
    }

    @Override
    public SuccessResponse resetPassword(String authToken, String newPassword) {
        String username = jwtService.extractUsername(authToken);
        log.info("Trying to change password for user: {}", username);
        var tokenMetadata = Optional.ofNullable(tokenMetadataRepository.findByUsernameOrderByCreatedAtDesc(username))
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getErrorCode(),
                        ExceptionEnum.USERNAME_NOT_FOUND.getMessage(username)));
        Boolean isValid = jwtService.validateToken(tokenMetadata.getToken(), username);
        if (isValid && Objects.equals(tokenMetadata.getToken(), authToken)) {
            User user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(
                    () -> new FlowBreakerException(ExceptionEnum.USERNAME_NOT_FOUND.getErrorCode(),
                            ExceptionEnum.USERNAME_NOT_FOUND.getMessage(username))
            );
            user.setPassword(encoder.encode(newPassword));
            user = Optional.of(userRepository.save(user)).orElseThrow(
                    () -> new FlowBreakerException(ExceptionEnum.UNABLE_TO_SAVE_DATA.getErrorCode(),
                            ExceptionEnum.UNABLE_TO_SAVE_DATA.getMessage())
            );
        }
        return new SuccessResponse(true,
                MessageFormat.format("Password Change Successfully for username: {0}", username), username);
    }

    @Override
    public SuccessResponse getUsers(String usernamePrefix) {
        log.info("Finding all Users starting with Username : {}", usernamePrefix);
        List<User> users = userRepository.findByUsernameStartsWith(usernamePrefix);
        log.info("Found {} users starting with username : {}", users.size(), usernamePrefix);
        return new SuccessResponse(true, String.format("Found %d users", users.size()), users);
    }
}
