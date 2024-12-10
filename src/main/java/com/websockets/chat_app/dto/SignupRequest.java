package com.websockets.chat_app.dto;

public record SignupRequest(
        String username,
        String password
) {}
