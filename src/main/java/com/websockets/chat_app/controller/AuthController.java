package com.websockets.chat_app.controller;

import com.websockets.chat_app.dto.AuthResponse;
import com.websockets.chat_app.dto.LoginRequest;
import com.websockets.chat_app.dto.SignupRequest;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.UserService;
import com.websockets.chat_app.service.impl.FileStorageService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;  // Add this

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService,
            FileStorageService fileStorageService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@ModelAttribute SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.username())) {
            return ResponseEntity.badRequest()
                    .body("Username already taken");
        }

        User user = new User();
        user.setUsername(signupRequest.username());
        user.setPassword(signupRequest.password());

        // Handle profile picture if provided
        if (signupRequest.profilePicture() != null && !signupRequest.profilePicture().isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(signupRequest.profilePicture());
                user.setProfilePicture(fileName);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body("Could not upload profile picture");
            }
        }

        User savedUser = userService.registerUser(user);
        String jwt = jwtService.generateToken(savedUser);

        // Include profile picture URL in response
        record AuthResponseWithPicture(String token, String username, String profilePicture) {}
        return ResponseEntity.ok(new AuthResponseWithPicture(
                jwt,
                savedUser.getUsername(),
                savedUser.getProfilePicture()
        ));
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

        // Include profile picture URL in login response too
        record AuthResponseWithPicture(String token, String username, String profilePicture) {}
        return ResponseEntity.ok(new AuthResponseWithPicture(
                jwt,
                user.getUsername(),
                user.getProfilePicture()
        ));
    }
}

//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserService userService;
//    private final JwtService jwtService;
//
//    public AuthController(
//            AuthenticationManager authenticationManager,
//            UserService userService,
//            JwtService jwtService
//    ) {
//        this.authenticationManager = authenticationManager;
//        this.userService = userService;
//        this.jwtService = jwtService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.username(),
//                        loginRequest.password()
//                )
//        );
//
//        User user = userService.findByUsername(loginRequest.username())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String jwt = jwtService.generateToken(user);
//
//        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername()));
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
//        if (userService.existsByUsername(signupRequest.username())) {
//            return ResponseEntity.badRequest()
//                    .body("Username already taken");
//        }
//
//        User user = new User();
//        user.setUsername(signupRequest.username());
//        user.setPassword(signupRequest.password());
//
//        User savedUser = userService.registerUser(user);
//        String jwt = jwtService.generateToken(savedUser);
//
//        return ResponseEntity.ok(new AuthResponse(jwt, savedUser.getUsername()));
//    }
//}