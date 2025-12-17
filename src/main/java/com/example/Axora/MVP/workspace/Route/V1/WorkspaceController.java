package com.example.Axora.MVP.workspace.Route.V1;

import com.example.Axora.MVP.workspace.DTO.request.*;
import com.example.Axora.MVP.workspace.DTO.response.WorkspaceResponse;
import com.example.Axora.MVP.workspace.Entity.Workspace;
import com.example.Axora.MVP.workspace.Exception.Workspace.WorkspaceNotFoundException;
import com.example.Axora.MVP.workspace.Serivce.UserWorkspaceRoleService;
import com.example.Axora.MVP.workspace.Serivce.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    @Autowired
    private final WorkspaceService workspaceService;

    @Autowired
    private final UserWorkspaceRoleService userWorkspaceRoleService;

    @PostMapping
    public WorkspaceResponse createWorkspace(@RequestBody CreateWorkspaceRequest request){

        Workspace ws = workspaceService.createWorkspace(request.getName(), request.getDescription());

        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(ws.getId());
        response.setName(ws.getName());
        response.setDescription(ws.getDescription());
        response.setOwnerUserId(ws.getOwner().getId());

        return response;
    }

    @GetMapping("/{workspaceId}")
    public WorkspaceResponse getWorkspace(@PathVariable UUID workspaceId,
                                          @RequestHeader("X-USER-ID") UUID userId) {

        userWorkspaceRoleService.requireRole(workspaceId, userId, "OWNER", "ADMIN", "EDITOR");

        Workspace ws = workspaceService.getWorkspace(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found"));

        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(ws.getId());
        response.setName(ws.getName());
        response.setDescription(ws.getDescription());
        response.setOwnerUserId(ws.getOwner().getId());

        return response;
    }

    @PutMapping("/{workspaceId}")
    public WorkspaceResponse updateWorkspace(@PathVariable UUID workspaceId,
                                             @RequestBody UpdateWorkspaceRequest request,
                                             @RequestHeader("X-USER-ID") UUID userId) {

        userWorkspaceRoleService.requireRole(workspaceId, userId, "OWNER", "ADMIN", "EDITOR");

        Workspace updated = workspaceService.updateWorkspace(
                workspaceId,
                request.getName(),
                request.getDescription()
        );

        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(updated.getId());
        response.setName(updated.getName());
        response.setDescription(updated.getDescription());
        response.setOwnerUserId(updated.getOwner().getId());

        return response;
    }
}
