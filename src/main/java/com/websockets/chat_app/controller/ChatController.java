package com.websockets.chat_app.controller;

import com.websockets.chat_app.entity.ChatMessage;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.ChatService;
import com.websockets.chat_app.service.UserService;
import com.websockets.chat_app.service.impl.FileStorageService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequestMapping("/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;

    public ChatController(ChatService chatService, UserService userService , JwtService jwtService , FileStorageService fileStorageService) {
        this.chatService = chatService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.fileStorageService = fileStorageService;
    }

    @MessageMapping("/chat.private")
    public void handlePrivateMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        // Get sender's profile picture
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

        // Log file message details for debugging
        if (message.getType() == ChatMessage.MessageType.FILE) {
            System.out.println("Received file message: " + message.getFileName());
            System.out.println("File type: " + message.getFileType());
        }

        chatService.sendPrivateMessage(message);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        User sender = userService.findByUsername(chatMessage.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));
        chatMessage.setSenderProfilePicture(sender.getProfilePicture());
        return chatMessage;
    }


//    @GetMapping("/messages/{username}")
//    @PreAuthorize("isAuthenticated()")  // Add this annotation
//    public ResponseEntity<List<ChatMessage>> getMessageHistory(
//            @PathVariable String username,
//            @RequestHeader("Authorization") String token) {
//        String currentUsername = jwtService.getUsernameFromToken(token.replace("Bearer ", ""));
//        // Implement message history retrieval
//        return ResponseEntity.ok(new ArrayList<>());
//    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender,
            @RequestParam("receiver") String receiver) {

        try {
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @MessageMapping("/chat.group")
    public void handleGroupMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        // Get sender's profile picture
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

        System.out.println("Received group message: " + message.getContent());
        System.out.println("For group: " + message.getGroupId());

        chatService.sendGroupMessage(message);
    }

//    @PostMapping("/upload-audio")
//    public ResponseEntity<ChatMessage> uploadAudioFile(
//            @RequestParam("audio") MultipartFile file,
//            @RequestParam("sender") String sender,
//            @RequestParam("receiver") String receiver) {
//
//        try {
//            String fileName = fileStorageService.storeFile(file);
//            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/uploads/")
//                    .path(fileName)
//                    .toUriString();
//
//            ChatMessage message = new ChatMessage();
//            message.setType(ChatMessage.MessageType.FILE);
//            message.setFileName(file.getOriginalFilename());
//            message.setFileType(file.getContentType());
//            message.setAudioUrl(fileUrl);
//            message.setAudioType(file.getContentType());
//
//            return ResponseEntity.ok(message);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new ChatMessage());
//        }
//    }

    @PostMapping("/upload-group")
    public ResponseEntity<Map<String, String>> uploadGroupFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender,
            @RequestParam("groupId") Long groupId) {

        try {
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}