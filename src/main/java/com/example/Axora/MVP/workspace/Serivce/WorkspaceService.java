package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.security.SecurityUtils;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.User.UserNotFoundException;
import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.DTO.response.WorkspaceResponse;
import com.example.Axora.MVP.workspace.Entity.*;
import com.example.Axora.MVP.workspace.Exception.Workspace.WorkspaceNotFoundException;
import com.example.Axora.MVP.workspace.Mapper.WorkspaceMapper;
import com.example.Axora.MVP.workspace.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceRolePermissionRepository workspaceRolePermissionRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final UserWorkspaceRoleRepository userWorkspaceRoleRepository;
    private final UserRepository userRepository;

    private final WorkspaceMapper workspaceMapper;

    @Transactional
    public Workspace createWorkspace(String name, String description){

        UUID currentUser = SecurityUtils.getCurrentUser();

        User owner = userRepository.findById(currentUser)
                .orElseThrow(() -> new UserNotFoundException("User Not Found with ID : "+ currentUser));

        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setDescription(description);
        workspace.setOwner(owner);

        return workspaceRepository.save(workspace);
    }

    public Optional<Workspace> getWorkspace(UUID workspaceId){
        return workspaceRepository.findById(workspaceId);
    }

    public List<WorkspaceResponse> getWorksapceList() {
        return workspaceRepository.findAll()
                .stream()
                .map(workspaceMapper::mapToResponse)
                .toList();
    }

    @Transactional
    public Workspace updateWorkspace(UUID workspaceId, String newName, String newDescription){
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found"));

        workspace.setName(newName);
        workspace.setDescription(newDescription);

        return workspaceRepository.save(workspace);
    }

    public void deteleWorkspace(UUID workspaceId){
        workspaceRepository.deleteById(workspaceId);
    }


}
