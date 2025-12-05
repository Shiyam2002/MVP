package com.example.Axora.MVP.user.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private String deviceInfo;
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private Timestamp expiresAt;
    private boolean revoked;

    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}