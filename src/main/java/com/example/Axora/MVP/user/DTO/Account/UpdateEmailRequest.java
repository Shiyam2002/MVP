package com.example.Axora.MVP.user.DTO.Account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(
        @NotBlank @Email
        String newEmail
) {}
