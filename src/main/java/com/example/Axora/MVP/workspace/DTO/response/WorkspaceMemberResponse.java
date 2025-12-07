package com.example.Axora.MVP.workspace.DTO.response;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkspaceMemberResponse {

    private UUID userId;
    private String username;
    private String roleName;
}
