package com.example.Axora.MVP.user.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@Data
public class UserSession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String deviceInfo;
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private Timestamp expiresAt;
    private boolean revoked;

    private Timestamp createAt;

    @PrePersist
    public void prePersist() {
        createAt = new Timestamp(System.currentTimeMillis());
    }
}
