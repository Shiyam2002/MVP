package com.example.Axora.MVP.workspace.Repository;

import com.example.Axora.MVP.workspace.Entity.WorkspacePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkspacePermissionRepository extends JpaRepository<WorkspacePermission, UUID> {
}
