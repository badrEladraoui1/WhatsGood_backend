package com.websockets.chat_app.service.impl;

import com.websockets.chat_app.entity.ChatMessage;
import com.websockets.chat_app.entity.Group;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.service.ChatService;
import com.websockets.chat_app.service.GroupService;
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
    private final GroupService groupService;

    public ChatServiceImpl(UserService userService, SimpMessagingTemplate messagingTemplate , JwtService jwtService , GroupService groupService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.jwtService = jwtService;
        this.groupService = groupService;
    }

    @Override
    public void sendPrivateMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

        // Send to receiver
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );

        // Send back to sender
        messagingTemplate.convertAndSendToUser(
                message.getSender(),
                "/queue/messages",
                message
        );
    }
//    public void sendPrivateMessage(ChatMessage message) {
//        message.setTimestamp(LocalDateTime.now());
//        User sender = userService.findByUsername(message.getSender())
//                .orElseThrow(() -> new RuntimeException("Sender not found"));
//        message.setSenderProfilePicture(sender.getProfilePicture());
//
//        messagingTemplate.convertAndSendToUser(
//                message.getReceiver(),
//                "/queue/messages",
//                message
//        );
//    }

    @Override
    public void notifyUserStatus(String username, boolean isOnline, String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }
        messagingTemplate.convertAndSend("/topic/status",
                Map.of("username", username, "isOnline", isOnline));
    }
//    public void notifyUserStatus(String username, boolean isOnline, String token) {
//        if (!jwtService.isTokenValid(token)) {
//            throw new RuntimeException("Invalid token");
//        }
//
//        messagingTemplate.convertAndSend("/topic/status",
//                Map.of("username", username, "isOnline", isOnline));
//    }

    @Override
    public void sendGroupMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        User sender = userService.findByUsername(message.getSender())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        message.setSenderProfilePicture(sender.getProfilePicture());

        Group group = groupService.getGroupById(message.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Send to all group members
        for (User member : group.getMembers()) {
            messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    "/queue/group-messages",
                    message
            );
        }
    }
}
