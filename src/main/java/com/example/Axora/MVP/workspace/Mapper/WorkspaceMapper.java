package com.example.Axora.MVP.workspace.Mapper;

import com.example.Axora.MVP.workspace.DTO.response.WorkspaceResponse;
import com.example.Axora.MVP.workspace.Entity.Workspace;

public interface WorkspaceMapper {
    WorkspaceResponse mapToResponse(Workspace workspace);
}
