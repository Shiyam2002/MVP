package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.workspace.Entity.WorkspacePermission;
import com.example.Axora.MVP.workspace.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WorkspacePermissionService {

    private final WorkspacePermissionRepository workspacePermissionRepository;

    @Transactional
    public WorkspacePermission createWorkspacePermission(String code, String description){
        WorkspacePermission workspacePermission = new WorkspacePermission();
        workspacePermission.setCode(code);
        workspacePermission.setDescription(description);
        return workspacePermissionRepository.save(workspacePermission);
    }

    public List<WorkspacePermission> getWorkspacePermission(){
        return workspacePermissionRepository.findAll();
    }
}
