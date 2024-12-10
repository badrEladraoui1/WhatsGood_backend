package com.websockets.chat_app.service.impl;

import com.websockets.chat_app.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String nomComplet) throws UsernameNotFoundException {
        com.websockets.chat_app.entity.User user = userRepo.findByUsername(nomComplet)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}