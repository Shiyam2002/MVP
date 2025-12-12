package com.example.Axora.MVP.user.DTO.User;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        String avatarUrl,
        String bio,
        String timezone
) {}
