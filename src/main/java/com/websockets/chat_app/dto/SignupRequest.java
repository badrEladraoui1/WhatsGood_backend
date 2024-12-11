package com.websockets.chat_app.dto;

import org.springframework.web.multipart.MultipartFile;

public record SignupRequest(
        String username,
        String password,
        MultipartFile profilePicture
) {}
