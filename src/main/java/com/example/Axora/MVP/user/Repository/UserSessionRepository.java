package com.example.Axora.MVP.user.Repository;

import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByRefreshTokenAndRevokedFalse(String refreshToken);
    List<UserSession> findByAccountAndRevokedFalse(Account account);

}
