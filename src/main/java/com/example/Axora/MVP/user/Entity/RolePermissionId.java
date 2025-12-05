package com.example.Axora.MVP.user.Entity;

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
public class RolePermissionId implements Serializable {

    private UUID roleId;
    private UUID permissionId;
}
