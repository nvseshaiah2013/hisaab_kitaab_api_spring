package com.kitaab.hisaab.ledger.config;

import com.kitaab.hisaab.ledger.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.ALLOW_USERS_ENDPOINT;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.CHANGE_PASSWORD_ENDPOINT;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${allowedOrigins}")
    private List<String> allowedOrigins;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(ALLOW_USERS_ENDPOINT)
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS)
                        .permitAll()
                        .requestMatchers(CHANGE_PASSWORD_ENDPOINT)
                        .authenticated()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                    cors.configurationSource(source -> getCorsConfiguration()).configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public CorsConfiguration getCorsConfiguration() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        return corsConfiguration;
    }
}
