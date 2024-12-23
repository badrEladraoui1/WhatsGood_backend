package com.websockets.chat_app.controller;

import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.UserService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatters")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class UsersController {

    private final UserService userService;
    private final JwtService jwtService;

    public UsersController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(userService.getAllUsersExcept(username));
    }
}
