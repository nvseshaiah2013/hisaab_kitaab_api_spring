package com.kitaab.hisaab.ledger.filter;

import com.kitaab.hisaab.ledger.dto.response.ErrorMessage;
import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.service.JwtService;
import com.kitaab.hisaab.ledger.service.impl.LoginUserServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.AUTHORIZATION_ERROR;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.AUTHORIZATION_HEADER;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BEARER_PREFIX;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LoginUserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String username = null;
        String jwt = null;

        try {
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                jwt = authHeader.substring(7);
                username = jwtService.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = (CustomUserDetails) this.userService.loadUserByUsername(username);
                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        } catch (JwtException e) {
            logger.error("Exception occurred while verifying JWT : {}", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(new ErrorMessage(AUTHORIZATION_ERROR, e.getMessage()));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(request, response);
    }
}