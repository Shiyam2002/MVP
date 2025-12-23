package com.example.Axora.MVP.security.dto;

import jakarta.validation.constraints.NotBlank;

public record UserResponse(
        @NotBlank String name,
        @NotBlank String email
) {
}
