package com.example.Axora.MVP.workspace.Repository;

import com.example.Axora.MVP.workspace.Entity.UserWorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserWorkspaceRoleRepository extends JpaRepository<UserWorkspaceRole, UUID> {
    List<UserWorkspaceRole> findByWorkspaceId(UUID workspaceId);

    List<UserWorkspaceRole> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
