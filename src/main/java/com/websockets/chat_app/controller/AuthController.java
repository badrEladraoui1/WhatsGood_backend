package com.websockets.chat_app.controller;

import com.websockets.chat_app.dto.AuthResponse;
import com.websockets.chat_app.dto.LoginRequest;
import com.websockets.chat_app.dto.SignupRequest;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.UserService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        User user = userService.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.username())) {
            return ResponseEntity.badRequest()
                    .body("Username already taken");
        }

        User user = new User();
        user.setUsername(signupRequest.username());
        user.setPassword(signupRequest.password());

        User savedUser = userService.registerUser(user);
        String jwt = jwtService.generateToken(savedUser);

        return ResponseEntity.ok(new AuthResponse(jwt, savedUser.getUsername()));
    }
}