package com.example.Axora.MVP.workspace.Entity;

import com.example.Axora.MVP.user.Entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "user_workspace_roles")
@Data
@NoArgsConstructor
public class UserWorkspaceRole {

    @EmbeddedId
    private UserWorkspaceRoleId id = new UserWorkspaceRoleId();

    @ManyToOne
    @MapsId("workspaceId")
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private WorkspaceRole role;

    private Timestamp assignedAt;

    public UserWorkspaceRole(Workspace workspace, User user, WorkspaceRole role) {
        this.workspace = workspace;
        this.user = user;
        this.role = role;

        this.id = new UserWorkspaceRoleId(workspace.getId(), user.getId());
        this.assignedAt = new Timestamp(System.currentTimeMillis());
    }
}

