package com.example.Axora.MVP.workspace.Serivce;

import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.UserNotFoundException;
import com.example.Axora.MVP.user.Repository.UserRepository;
import com.example.Axora.MVP.workspace.Entity.Workspace;
import com.example.Axora.MVP.workspace.Entity.WorkspaceInvitation;
import com.example.Axora.MVP.workspace.Exception.Workspace.WorkspaceNotFoundException;
import com.example.Axora.MVP.workspace.Exception.WorkspaceInvitation.WorkspaceInvitationNotFoundException;
import com.example.Axora.MVP.workspace.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WorkspaceInvitationService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final UserRepository userRepository;

    @Autowired
    private WorkspaceRoleService workspaceRoleService;

    @Transactional
    public WorkspaceInvitation inviteUser(UUID workspaceId, String email, UUID inviteByUserId){
        Workspace workspace =workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace Not Found"));

        User inviter = userRepository.findById(inviteByUserId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        WorkspaceInvitation invitation = new WorkspaceInvitation();

        invitation.setWorkspace(workspace);
        invitation.setEmail(email);
        invitation.setInvitedBy(inviter);
        invitation.setStatus("pending");
        invitation.setToken(UUID.randomUUID().toString());

        return workspaceInvitationRepository.save(invitation);
    }

    public Optional<WorkspaceInvitation> getInvitation(UUID invitationId) {
        return workspaceInvitationRepository.findById(invitationId);
    }

    @Transactional
    public void acceptInvitation(UUID invitationId, UUID userId){
        WorkspaceInvitation invitation = workspaceInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new WorkspaceInvitationNotFoundException("Invitation Not Found"));

        invitation.setStatus("accepted");

        workspaceInvitationRepository.save(invitation);

        workspaceRoleService.assignWorkspaceUserRole(invitation.getWorkspace().getId(), userId, "VIEWER");
    }

    public void declineInvitation(UUID invitationId){
        WorkspaceInvitation invitation = workspaceInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new WorkspaceInvitationNotFoundException("Invitation not found"));

        invitation.setStatus("declined");

        workspaceInvitationRepository.save(invitation);
    }
}
