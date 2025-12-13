package com.example.Axora.MVP.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min=8) String newPassword
) {}
