package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.User.UserNotFoundException;
import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.Entity.*;
import com.example.Axora.MVP.workspace.Exception.Workspace.WorkspaceNotFoundException;
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

    @Transactional
    public Workspace createWorkspace(String name, String description, UUID owneruserID){
        User owner = userRepository.findById(owneruserID)
                .orElseThrow(() -> new UserNotFoundException("User Not Found with ID : "+ owneruserID));

        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setDescription(description);
        workspace.setOwner(owner);

        Workspace saved = workspaceRepository.save(workspace);

        return saved;
    }

    public Optional<Workspace> getWorkspace(UUID workspaceId){
        return workspaceRepository.findById(workspaceId);
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
