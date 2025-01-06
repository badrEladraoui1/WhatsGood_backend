package com.websockets.chat_app.controller;

import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;
    private final Path uploadDir;

    public UserProfileController(
            UserService userService,
            @Value("${app.upload.dir:uploads}") String uploadPath) {
        this.userService = userService;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath();
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<?> getProfilePicture(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            if (user.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }

            Path imagePath = uploadDir.resolve(user.getProfilePicture());

            if (!Files.exists(imagePath)) {
                System.out.println("Image file not found: " + imagePath);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // or detect content type
                    .body(imageBytes);

        } catch (Exception e) {
            System.out.println("Error serving profile picture: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error serving profile picture");
        }
    }
}

