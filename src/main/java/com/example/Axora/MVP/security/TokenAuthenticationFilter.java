package com.example.Axora.MVP.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_COOKIE = "token";

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            resolveToken(request)
                    .flatMap(tokenProvider::validateAccessToken)
                    .ifPresent(jws -> {

                        // 🔐 Extract user identity from JWT
                        String email = jws.getPayload().get("email", String.class);

                        UserDetails userDetails =
                                userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        // 🔥 THIS sets the authenticated user
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    });

        } catch (Exception e) {
            log.error("Failed to authenticate request", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /* ==================================================
       TOKEN RESOLUTION (HEADER → COOKIE FALLBACK)
       ================================================== */

    private Optional<String> resolveToken(HttpServletRequest request) {

        // 1️⃣ Authorization header (Bearer token)
        String header = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX)) {
            return Optional.of(header.substring(TOKEN_PREFIX.length()));
        }

        // 2️⃣ HttpOnly cookie (token)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TOKEN_COOKIE.equals(cookie.getName())
                        && StringUtils.hasText(cookie.getValue())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }

        return Optional.empty();
    }
}
