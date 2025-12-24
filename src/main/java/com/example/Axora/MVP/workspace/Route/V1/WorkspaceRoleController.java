package com.example.Axora.MVP.workspace.Route.V1;

import com.example.Axora.MVP.workspace.DTO.request.AssignRoleRequest;
import com.example.Axora.MVP.workspace.DTO.response.WorkspaceMemberResponse;
import com.example.Axora.MVP.workspace.Entity.UserWorkspaceRole;
import com.example.Axora.MVP.workspace.Serivce.UserWorkspaceRoleService;
import com.example.Axora.MVP.workspace.Serivce.WorkspaceRoleService;
import com.example.Axora.MVP.workspace.Serivce.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workspace/role")
public class WorkspaceRoleController {

    @Autowired
    private final WorkspaceRoleService workspaceRoleService;

    @Autowired
    private final UserWorkspaceRoleService userWorkspaceRoleService;

    @PostMapping("/{workspaceId}/assign-role")
    public String assignRole(@PathVariable UUID workspaceId,
                             @RequestBody AssignRoleRequest request,
                             @RequestHeader("X-USER-ID") UUID userId){

        userWorkspaceRoleService.requireRole(workspaceId, userId, "OWNER", "ADMIN");

        workspaceRoleService.assignWorkspaceUserRole(workspaceId, request.getUserId(), request.getRoleName());

        return "Role assigned successfully";
    }

    @GetMapping("/{workspaceId}/members")
    public List<WorkspaceMemberResponse> getMembers(@PathVariable UUID workspaceId,
                                                    @RequestHeader("X-USER-ID") UUID userId){
        userWorkspaceRoleService.requireRole(workspaceId, userId, "OWNER", "ADMIN", "EDITOR");

        List<UserWorkspaceRole> members = workspaceRoleService.getWorkspaceMembers(workspaceId);

        return members.stream().map(m -> {
            WorkspaceMemberResponse r = new WorkspaceMemberResponse();
            r.setUserId(m.getUser().getId());
            r.setUsername(m.getUser().getUsername());
            r.setRoleName(m.getRole().getName());
            return r;
        }).collect(Collectors.toList());
    }
}
