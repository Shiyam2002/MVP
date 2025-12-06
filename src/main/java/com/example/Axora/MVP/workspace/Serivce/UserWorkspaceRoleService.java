package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.Entity.UserWorkspaceRole;
import com.example.Axora.MVP.workspace.Entity.WorkspaceRolePermission;
import com.example.Axora.MVP.workspace.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserWorkspaceRoleService {

    private final WorkspaceRolePermissionRepository workspaceRolePermissionRepository;
    private final UserWorkspaceRoleRepository userWorkspaceRoleRepository;

    // Permissions for a user inside a workspace

    public Set<String> getUserPermissions(UUID workspaceId, UUID userId){
        List<UserWorkspaceRole> roles = userWorkspaceRoleRepository.findByWorkspaceIdAndUserId(workspaceId, userId);

        if(roles.isEmpty()) return Set.of();

        Set<String> permissions = new HashSet<>();

        for (UserWorkspaceRole userRole : roles){
            List<WorkspaceRolePermission> rp =  workspaceRolePermissionRepository.findByRoleId(userRole.getRole().getId());

            for (WorkspaceRolePermission link: rp){
                permissions.add(link.getPermission().getCode());
            }
        }
        return permissions;
    }
}
