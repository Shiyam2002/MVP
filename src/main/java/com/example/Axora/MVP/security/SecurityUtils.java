package com.example.Axora.MVP.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return ((CustomUserDetails) Objects.requireNonNull(auth.getPrincipal())).getUserId();
    }
}
