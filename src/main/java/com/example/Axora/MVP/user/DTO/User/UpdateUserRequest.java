package com.example.Axora.MVP.user.DTO.User;

public record UpdateUserRequest(
        String username,
        String avatarurl,
        String bio,
        String timezone
) {}
