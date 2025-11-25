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

    @Value("${app.jwt.expiration.minutes}")
    private Long jwtExpirationMinutes;

    private SecretKey getSigningKey() {
        // Proper HS512 secure key handling
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generate(Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant now = Instant.now();

        return Jwts.builder()
                .header().add("typ", TOKEN_TYPE)
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(TOKEN_ISSUER)
                .audience().add(TOKEN_AUDIENCE)
                .and()
                .subject(user.getUsername())
                .claim("roles", roles)
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtExpirationMinutes * 60)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }


    public Optional<Jws<Claims>> validateTokenAndGetJws(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

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

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "axora-api";
    public static final String TOKEN_AUDIENCE = "axora-app";
}
