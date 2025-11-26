package com.example.Axora.MVP.security;

import com.example.Axora.MVP.user.Entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {this.user = user;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .filter(role -> role != null)
                .flatMap(role -> {
                    if (role.getPermissions() == null) {
                        return Stream.of(new SimpleGrantedAuthority(role.getName()));
                    }
                    return role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getCode()));
                })
                .toList();
    }


    @Override
    public @Nullable String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    public UUID getId() { return user.getId();}

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isEmailVerified(); }

    public String getEmail() { return user.getEmail(); }
}
