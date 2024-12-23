package com.websockets.chat_app.service.impl;

import com.websockets.chat_app.entity.ChatMessage;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.ChatService;
import com.websockets.chat_app.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService;

    public ChatServiceImpl(UserService userService, SimpMessagingTemplate messagingTemplate , JwtService jwtService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.jwtService = jwtService;
    }

    @Override
    public void sendPrivateMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );
    }

    @Override
    public void notifyUserStatus(String username, boolean isOnline, String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }

        messagingTemplate.convertAndSend("/topic/status",
                Map.of("username", username, "isOnline", isOnline));
    }
}
