package com.example.Axora.MVP.workspace.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
