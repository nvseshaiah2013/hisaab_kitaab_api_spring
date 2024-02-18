package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class LoginUserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetching details for username : {}", username);
        return Optional
                .ofNullable(userRepository.findByUsername(username))
                .map(u -> {
                    var user = new CustomUserDetails(u.getUsername(), u.getPassword(), new ArrayList<>());
                    user.put("name", u.getName());
                    return user;
                })
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}