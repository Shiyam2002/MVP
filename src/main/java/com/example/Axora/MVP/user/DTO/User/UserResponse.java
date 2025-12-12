package com.example.Axora.MVP.user.DTO.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String userName,
        String avatarUrl,
        String bio,
        String timezone
) {}
