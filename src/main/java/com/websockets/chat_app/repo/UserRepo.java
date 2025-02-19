package com.websockets.chat_app.repo;

import com.websockets.chat_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByUsernameNot(String username);
}
