package com.example.Axora.MVP.security.Repository;

import com.example.Axora.MVP.security.Entity.RolePermission;
import com.example.Axora.MVP.security.Entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}
