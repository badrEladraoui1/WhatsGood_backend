package com.websockets.chat_app.dto;


public record LoginRequest(
        String username,
        String password
) {}