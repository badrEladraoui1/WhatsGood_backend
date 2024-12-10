package com.websockets.chat_app.service;

import com.websockets.chat_app.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User registerUser(User user);

}
