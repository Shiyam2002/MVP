package com.example.Axora.MVP.security.Route.V1;

import com.example.Axora.MVP.security.TokenProvider;
import com.example.Axora.MVP.security.dto.AuthResponse;
import com.example.Axora.MVP.security.dto.LoginRequest;
import com.example.Axora.MVP.security.dto.SignUpRequest;
import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.User.DuplicatedUserInfoException;
import com.example.Axora.MVP.user.Service.account.AccountService;
import com.example.Axora.MVP.security.Service.RefreshTokenService;
import com.example.Axora.MVP.user.Service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final UserService userService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    // ================================
    // LOGIN (EMAIL + PASSWORD)
    // ================================
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // 1️⃣ Authenticate user
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.email(),
                                    loginRequest.password()
                            )
                    );

            // 2️⃣ Generate access token
            String accessToken =
                    tokenProvider.generateAccessToken(authentication);

            // 3️⃣ Fetch account
            Account account =
                    accountService.findByEmail(loginRequest.email());

            // 4️⃣ Generate refresh token
            String refreshToken =
                    tokenProvider.generateRefreshToken(
                            account.getId().toString()
                    );

            // 5️⃣ Store refresh session
            refreshTokenService.createSession(
                    account,
                    refreshToken,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent")
            );

            // 6️⃣ Set JWT as HttpOnly cookie (🔥 REQUIRED)
            ResponseCookie cookie = ResponseCookie.from("token", accessToken)
                    .httpOnly(true)
                    .secure(false)              // true in prod (HTTPS)
                    .sameSite("Lax")            // OK for localhost
                    .path("/")
                    .maxAge(60 * 60)            // 1 hour
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // 7️⃣ Return response body (optional but useful)
            return ResponseEntity.ok(
                    new AuthResponse(accessToken, refreshToken)
            );

        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }
    }


    // ================================
    // SIGNUP
    // ================================
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthResponse signUp(
            @Valid @RequestBody SignUpRequest signUpRequest,
            HttpServletRequest request
    ) {
        // Check duplicates
        if (userService.hasUserWithUsername(signUpRequest.username())) {
            throw new DuplicatedUserInfoException(
                    "Username %s already been used".formatted(signUpRequest.username()));
        }

        if (accountService.hasAccountWithEmail(signUpRequest.email())) {
            throw new DuplicatedUserInfoException(
                    "Email %s already been used".formatted(signUpRequest.email()));
        }

        // Create User entity (profile)
        User user = new User();
        user.setUsername(signUpRequest.username());
        userService.saveUser(user);

        // Create Account entity (credentials)
        Account account = new Account();
        account.setEmail(signUpRequest.email());
        account.setPasswordHash(signUpRequest.password()); // raw → encoded in service
        account.setUser(user);
        account.setEmailVerified(true);
        accountService.createAccount(account);

        // Auto login newly created account
        String accessToken = authenticateAndGetToken(
                signUpRequest.email(),
                signUpRequest.password()
        );

        String refreshToken = tokenProvider.generateRefreshToken(account.getId().toString());

        refreshTokenService.createSession(
                account,
                refreshToken,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return new AuthResponse(accessToken, refreshToken);
    }

    // ================================
    // Helper
    // ================================
    private String authenticateAndGetToken(String email, String password) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );

        return tokenProvider.generateAccessToken(authentication);
    }

    private String normalizeEmail(String email){
        return email == null ? null : email.trim().toLowerCase();
    }
}
