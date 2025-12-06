package com.example.Axora.MVP.workspace.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workspace_role_permissions")
@Data
@NoArgsConstructor
public class WorkspaceRolePermission {

    @EmbeddedId
    private WorkspaceRolePermissionId id = new WorkspaceRolePermissionId();

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private WorkspaceRole role;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private WorkspacePermission permission;

    public WorkspaceRolePermission(WorkspaceRole role, WorkspacePermission permission) {
        this.role = role;
        this.permission = permission;
        this.id = new WorkspaceRolePermissionId(role.getId(), permission.getId());
    }
}
