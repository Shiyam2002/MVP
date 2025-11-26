package com.example.Axora.MVP.user.Repository;

import com.example.Axora.MVP.user.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
