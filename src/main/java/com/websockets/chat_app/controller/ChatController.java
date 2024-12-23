package com.websockets.chat_app.controller;

import com.websockets.chat_app.entity.ChatMessage;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.ChatService;
import com.websockets.chat_app.service.UserService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/chat")
@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final JwtService jwtService;

    public ChatController(ChatService chatService, UserService userService , JwtService jwtService) {
        this.chatService = chatService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @MessageMapping("/chat.private")
    public void handlePrivateMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        // Get sender's profile picture
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

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


    @GetMapping("/messages/{username}")
    @PreAuthorize("isAuthenticated()")  // Add this annotation
    public ResponseEntity<List<ChatMessage>> getMessageHistory(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        String currentUsername = jwtService.getUsernameFromToken(token.replace("Bearer ", ""));
        // Implement message history retrieval
        return ResponseEntity.ok(new ArrayList<>());
    }
}