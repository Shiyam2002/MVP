package com.example.Axora.MVP.user.Repository;

import com.example.Axora.MVP.user.Entity.UserRole;
import com.example.Axora.MVP.user.Entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
