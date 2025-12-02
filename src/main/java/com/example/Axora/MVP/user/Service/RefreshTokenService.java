package com.example.Axora.MVP.user.Service;

import com.example.Axora.MVP.security.TokenProvider;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Entity.UserSession;
import com.example.Axora.MVP.user.Repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserSessionRepository sessionRepository;
    private final TokenProvider tokenProvider;

    // 1. Create & store a new refresh session
    public UserSession createSession(User user, String refreshToken, String ip, String deviceInfo) {

        Instant expiresAt = Instant.now().plusSeconds( tokenProvider.getRefreshExpirationDays() );

        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .ipAddress(ip)
                .deviceInfo(deviceInfo)
                .expiresAt(Timestamp.from(expiresAt))
                .revoked(false)
                .build();

        return sessionRepository.save(session);
    }

    // 2. Validate refresh token and load session
    public Optional<UserSession> validateRefreshToken(String token) {

        // Validate JWT first
        if (tokenProvider.validateRefreshToken(token).isEmpty()) {
            return Optional.empty();
        }

        // Check DB session
        Optional<UserSession> sessionOpt = sessionRepository.findByRefreshTokenAndRevokedFalse(token);

        if (sessionOpt.isEmpty())
            return Optional.empty();

        UserSession session = sessionOpt.get();

        if (session.isRevoked())
            return Optional.empty();

        if (session.getExpiresAt().before(new Timestamp(System.currentTimeMillis())))
            return Optional.empty();

        return Optional.of(session);
    }


    // 3. Revoke a refresh token
    public void revokeSession(UserSession session) {
        session.setRevoked(true);
        sessionRepository.save(session);
    }

    // 4. Revoke ALL user sessions (e.g., user logs out all devices)
    public void revokeAllUserSessions(User user) {
        sessionRepository.findByUserAndRevokedFalse(user)
                .forEach(s -> {
                    s.setRevoked(true);
                    sessionRepository.save(s);
                });
    }

    // 5. Extend TokenProvider for expiration
    public long getRefreshTokenValiditySeconds() {
        return tokenProvider.getRefreshExpirationDays();
    }
}
