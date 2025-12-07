package com.example.Axora.MVP.workspace.DTO.response;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkspaceResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID ownerUserId;
}
