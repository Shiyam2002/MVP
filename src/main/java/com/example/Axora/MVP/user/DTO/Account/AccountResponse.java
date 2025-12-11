package com.example.Axora.MVP.user.DTO.Account;

import com.example.Axora.MVP.security.Entity.Role;

import java.util.Set;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String email,
        String phone,
        String status,
        Boolean emailVerified,
        Boolean phoneVerified,
        String authProvider,
        Set<Role> role
) {}
