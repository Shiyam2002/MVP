package com.example.Axora.MVP.security.Repository;

import com.example.Axora.MVP.security.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
