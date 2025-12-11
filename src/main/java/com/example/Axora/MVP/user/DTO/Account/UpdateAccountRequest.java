package com.example.Axora.MVP.user.DTO.Account;

public record UpdateAccountRequest(
        String email,
        String phone,
        String status,
        Boolean emailVerified,
        Boolean phoneVerified,
        String providerId,
        String authProvider
) {}
