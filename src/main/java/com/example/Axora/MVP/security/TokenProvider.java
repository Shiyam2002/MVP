package com.example.Axora.MVP.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.access.expiration.minutes}")
    private Long accessExpirationMinutes;

    @Value("${app.refresh.expiration.days}")
    private Long refreshExpirationDays;

    private SecretKey getSigningKey() {
        // Proper HS512 secure key handling
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateAccessToken(Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant now = Instant.now();

        return Jwts.builder()
                .header().add("typ", ACCESS_TOKEN_TYPE)
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(TOKEN_ISSUER)
                .audience().add(TOKEN_AUDIENCE)
                .and()

                // IMPORTANT CHANGE
                .subject(user.getAccountId().toString())

                .claim("userId", user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("roles", roles)

                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessExpirationMinutes * 60)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }



    public Optional<Jws<Claims>> validateAccessToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            if (!ACCESS_TOKEN_TYPE.equals(jws.getHeader().get("typ"))) {
                log.warn("❌ Not an access token");
                return Optional.empty();
            }


            return Optional.of(jws);

        } catch (ExpiredJwtException e) {
            log.warn("❌ Expired JWT: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("❌ Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("❌ Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("❌ Invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("❌ Empty or null JWT: {}", e.getMessage());
        }

        return Optional.empty();
    }


    public String generateRefreshToken(String userId) {

        Instant now = Instant.now();

        return Jwts.builder()
                .header().add("typ", REFRESH_TOKEN_TYPE)
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(TOKEN_ISSUER)
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshExpirationDays * 24 * 60 * 60)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Optional<Jws<Claims>> validateRefreshToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            if (!REFRESH_TOKEN_TYPE.equals(jws.getHeader().get("typ"))) {
                log.warn("❌ Header type is not refresh");
                return Optional.empty();
            }

            // ❗ Reject if not a refresh token
            if (!"refresh".equals(jws.getPayload().get("type"))) {
                log.warn("❌ Token is not a refresh token");
                return Optional.empty();
            }

            return Optional.of(jws);

        } catch (ExpiredJwtException e) {
            log.warn("❌ Refresh Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            log.warn("❌ Invalid Refresh Token: {}", e.getMessage());
        }

        return Optional.empty();
    }

    public Long getRefreshExpirationDays() {
        return refreshExpirationDays;
    }

    public static final String REFRESH_TOKEN_TYPE = "refresh";
    public static final String ACCESS_TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "axora-api";
    public static final String TOKEN_AUDIENCE = "axora-app";
}
