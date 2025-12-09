package com.example.Axora.MVP.workspace.Route.V1;

import com.example.Axora.MVP.workspace.DTO.request.AcceptInviteRequest;
import com.example.Axora.MVP.workspace.DTO.request.InviteUserRequest;
import com.example.Axora.MVP.workspace.DTO.response.InvitationResponse;
import com.example.Axora.MVP.workspace.Entity.WorkspaceInvitation;
import com.example.Axora.MVP.workspace.Serivce.UserWorkspaceRoleService;
import com.example.Axora.MVP.workspace.Serivce.WorkspaceInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invite")
public class WorkspaceInvitationController {

    @Autowired
    private final UserWorkspaceRoleService userWorkspaceRoleService;

    @Autowired
    private final WorkspaceInvitationService workspaceInvitationService;


    @PostMapping("/{workspaceId}/invite")
    public InvitationResponse invite(@PathVariable UUID workspaceId,
                                     @RequestBody InviteUserRequest req,
                                     @RequestHeader("X-USER-ID") UUID inviterId) {

        userWorkspaceRoleService.requireRole(workspaceId, inviterId, "OWNER", "ADMIN");

        WorkspaceInvitation inv =
                workspaceInvitationService.inviteUser(workspaceId, req.getEmail(), inviterId);

        InvitationResponse r = new InvitationResponse();
        r.setId(inv.getId());
        r.setEmail(inv.getEmail());
        r.setStatus(inv.getStatus());
        r.setToken(inv.getToken());

        return r;
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public String acceptInvite(@PathVariable UUID invitationId,
                               @RequestBody AcceptInviteRequest req) {

        workspaceInvitationService.acceptInvitation(invitationId, req.getUserId());
        return "Invitation accepted";
    }


    @PostMapping("/invitations/{invitationId}/decline")
    public String declineInvite(@PathVariable UUID invitationId) {
        workspaceInvitationService.declineInvitation(invitationId);
        return "Invitation declined";
    }

}
