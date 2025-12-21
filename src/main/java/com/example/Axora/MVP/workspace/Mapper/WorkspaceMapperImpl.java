package com.example.Axora.MVP.workspace.Mapper;

import com.example.Axora.MVP.workspace.DTO.response.WorkspaceResponse;
import com.example.Axora.MVP.workspace.Entity.Workspace;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {

    @Override
    public WorkspaceResponse mapToResponse(Workspace workspace) {
        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setDescription(workspace.getDescription());
        response.setOwnerUserId(workspace.getOwner().getId());

        return response;
    }
}
