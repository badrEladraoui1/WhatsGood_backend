package com.websockets.chat_app.service;

import com.websockets.chat_app.entity.ChatMessage;

public interface ChatService {
     void sendPrivateMessage(ChatMessage message);
//     void notifyUserStatus(String username, boolean isOnline , String token);
     void sendGroupMessage(ChatMessage message);
}
