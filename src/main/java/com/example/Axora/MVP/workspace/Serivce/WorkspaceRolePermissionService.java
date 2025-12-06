package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.Entity.WorkspacePermission;
import com.example.Axora.MVP.workspace.Entity.WorkspaceRole;
import com.example.Axora.MVP.workspace.Entity.WorkspaceRolePermission;
import com.example.Axora.MVP.workspace.Exception.WorkspacePermission.WorkspacePermissionNotFoundException;
import com.example.Axora.MVP.workspace.Exception.WorkspaceRole.WorkspaceRoleNotFoundException;
import com.example.Axora.MVP.workspace.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WorkspaceRolePermissionService {

    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceRolePermissionRepository workspaceRolePermissionRepository;

    @Transactional
    public void assignPermissionToRole(UUID roleId, UUID permissionId){
        WorkspaceRole role = workspaceRoleRepository.findById(roleId)
                .orElseThrow(() -> new WorkspaceRoleNotFoundException("Role Not Found"));

        WorkspacePermission permission = workspacePermissionRepository.findById(permissionId)
                .orElseThrow(() -> new WorkspacePermissionNotFoundException("Permission Not Found"));

        WorkspaceRolePermission link = new WorkspaceRolePermission(role, permission);

        workspaceRolePermissionRepository.save(link);
    }
}
