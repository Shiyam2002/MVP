package com.example.Axora.MVP.security.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

public record LoginRequest(
         @NotBlank String email,
         @NotBlank String password) {
}
