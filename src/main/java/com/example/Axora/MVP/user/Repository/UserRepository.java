package com.example.Axora.MVP.user.Repository;

import com.example.Axora.MVP.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String Username);
    boolean existsByUsername(String username);
}
