package com.example.Axora.MVP.workspace.DTO.request;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignRoleRequest {
    private UUID userId;
    private String roleName;
}
