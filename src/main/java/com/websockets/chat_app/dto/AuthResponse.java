package com.websockets.chat_app.dto;

public record AuthResponse(
        String token,
        String username
) {}
