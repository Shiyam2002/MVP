package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.User.UserNotFoundException;
import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.Entity.UserWorkspaceRole;
import com.example.Axora.MVP.workspace.Entity.Workspace;
import com.example.Axora.MVP.workspace.Entity.WorkspaceRole;
import com.example.Axora.MVP.workspace.Exception.Workspace.WorkspaceNotFoundException;
import com.example.Axora.MVP.workspace.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WorkspaceRoleService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final UserWorkspaceRoleRepository userWorkspaceRoleRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkspaceRole createWorkspaceRole(String name, String description){
        WorkspaceRole role = new WorkspaceRole();
        role.setName(name);
        role.setDescription(description);
        return workspaceRoleRepository.save(role);
    }

    public List<WorkspaceRole> getAllWorkspaceRole(){
        return workspaceRoleRepository.findAll();
    }

    // User Role for Workspace

    @Transactional
    public void assignWorkspaceUserRole(UUID workspaceId, UUID userId, String roleName){
        WorkspaceRole role = workspaceRoleRepository.findByName(roleName);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not Found"));

        UserWorkspaceRole entity = new UserWorkspaceRole(workspace,user,role);

        userWorkspaceRoleRepository.save(entity);
    }

    public List<UserWorkspaceRole> getWorkspaceMembers(UUID workspaceId){
        return userWorkspaceRoleRepository.findByWorkspaceId(workspaceId);
    }
}
