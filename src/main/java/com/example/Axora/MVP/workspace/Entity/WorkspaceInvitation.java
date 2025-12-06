package com.example.Axora.MVP.workspace.Entity;

import com.example.Axora.MVP.user.Entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "workspace_invitations")
@Data
public class WorkspaceInvitation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    private String email;

    @ManyToOne
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    private String status; // pending, accepted, declined

    private String token;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}

