package com.example.Axora.MVP.user.DTO.Account;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank String roleName
) {}
