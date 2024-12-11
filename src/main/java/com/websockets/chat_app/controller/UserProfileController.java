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
//public class UserProfileController {
//
//    private final UserService userService;
//    private final FileStorageService fileStorageService;
//
//    public UserProfileController(UserService userService, FileStorageService fileStorageService) {
//        this.userService = userService;
//        this.fileStorageService = fileStorageService;
//    }
//
//    @PostMapping("/profile-picture")
//    public ResponseEntity<?> uploadProfilePicture(
//            @RequestParam("file") MultipartFile file,
//            Authentication authentication) {
//
//        try {
//            // Get current user
//            User user = userService.findByUsername(authentication.getName())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Delete old profile picture if exists
//            if (user.getProfilePicture() != null) {
//                fileStorageService.deleteFile(user.getProfilePicture());
//            }
//
//            // Store new file
//            String fileName = fileStorageService.storeFile(file);
//
//            // Update user profile picture path
//            user.setProfilePicture(fileName);
//            userService.save(user);
//
//            return ResponseEntity.ok().body("Profile picture updated successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Could not upload profile picture");
//        }
//    }
//
//    @GetMapping("/profile-picture/{username}")
//    public ResponseEntity<?> getProfilePicture(@PathVariable String username) {
//        try {
//            User user = userService.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            return ResponseEntity.ok().body(user.getProfilePicture());
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}

