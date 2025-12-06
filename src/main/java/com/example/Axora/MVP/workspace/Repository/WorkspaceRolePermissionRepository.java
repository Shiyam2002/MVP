package com.example.Axora.MVP.workspace.Repository;

import com.example.Axora.MVP.workspace.Entity.WorkspaceRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceRolePermissionRepository extends JpaRepository<WorkspaceRolePermission, UUID> {
    List<WorkspaceRolePermission> findByRoleId(Long id);
}
