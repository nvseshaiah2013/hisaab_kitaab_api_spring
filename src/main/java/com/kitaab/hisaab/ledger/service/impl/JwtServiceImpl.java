package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.config.JwtSecret;
import com.kitaab.hisaab.ledger.constants.ApplicationConstants;
import com.kitaab.hisaab.ledger.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
@AllArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtSecret jwtSecret;

    @Override
    public String extractUsername(String token) {
        log.debug("Extracting username from JWT Token");
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claims from the JWT Token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(jwtSecret.secretKey()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Creates a token from UserDetails which is valid for 1 day
     *
     * @param userDetails
     * @return
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        log.debug("Generating JWT Token for : {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), ApplicationConstants.DEFAULT_JWT_TOKEN_VALIDITY);
    }


    /**
     * Create a token from username which is valid for 10 minutes
     *
     * @param username
     * @return
     */
    @Override
    public String generateToken(String username) {
        log.debug("Generating JWT Token for : {}", username);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, ApplicationConstants.RESET_PASSWORD_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String subject, Long validity) {
        log.debug("Generating Jwt Token for username: {}", subject);
        return Jwts.builder()
                .claims(claims)
                .id(subject)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(jwtSecret.secretKey()))
                .compact();
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        log.debug("Validating JWT for the user : {}", userDetails.getUsername());
        final String username = extractUsername(token);
        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public Boolean validateToken(String token, String username) {
        log.debug("Validating JWT for the user : {}", username);
        final String usernameFromToken = extractUsername(token);
        return Objects.equals(username, usernameFromToken) && !isTokenExpired(token);
    }
}