package com.example.Axora.MVP.workspace.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserWorkspaceRoleId implements Serializable {

    private UUID workspaceId;
    private UUID userId;
}
