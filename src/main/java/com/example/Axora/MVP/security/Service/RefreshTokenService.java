package com.example.Axora.MVP.security.Service;

import com.example.Axora.MVP.security.TokenProvider;
import com.example.Axora.MVP.user.Entity.Account;
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
    public UserSession createSession(Account account, String refreshToken, String ip, String deviceInfo) {

        // Fix: convert days → seconds
        Instant expiresAt = Instant.now()
                .plusSeconds(tokenProvider.getRefreshExpirationDays() * 24 * 60 * 60);

        UserSession session = UserSession.builder()
                .account(account) // FIXED: use account
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

        // Step 1: Validate JWT
        if (tokenProvider.validateRefreshToken(token).isEmpty()) {
            return Optional.empty();
        }

        // Step 2: Check DB session
        Optional<UserSession> sessionOpt =
                sessionRepository.findByRefreshTokenAndRevokedFalse(token);

        if (sessionOpt.isEmpty()) return Optional.empty();

        UserSession session = sessionOpt.get();

        if (session.isRevoked()) return Optional.empty();

        if (session.getExpiresAt().before(new Timestamp(System.currentTimeMillis())))
            return Optional.empty();

        return Optional.of(session);
    }

    // 3. Revoke a refresh token
    public void revokeSession(UserSession session) {
        session.setRevoked(true);
        sessionRepository.save(session);
    }

    // 4. Revoke ALL sessions for given account
    public void revokeAllAccountSessions(Account account) {
        sessionRepository.findByAccountAndRevokedFalse(account)
                .forEach(s -> {
                    s.setRevoked(true);
                    sessionRepository.save(s);
                });
    }

    // 5. (Optional utility)
    public long getRefreshTokenValiditySeconds() {
        return tokenProvider.getRefreshExpirationDays() * 24 * 60 * 60;
    }
}
