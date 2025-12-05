package com.example.Axora.MVP.user.Entity;

import com.example.Axora.MVP.security.Entity.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    private String authProvider;
    private String providerId;

    private String status; // active, disabled

    private boolean emailVerified;
    private boolean phoneVerified;

    private Timestamp lastLoginAt;

    private Integer refreshTokenVersion;

    private String passwordResetToken;
    private Timestamp passwordResetExpiresAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
