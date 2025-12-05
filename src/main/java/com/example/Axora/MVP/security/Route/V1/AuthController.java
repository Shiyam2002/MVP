package com.example.Axora.MVP.security.Route.V1;

import com.example.Axora.MVP.security.TokenProvider;
import com.example.Axora.MVP.security.dto.AuthResponse;
import com.example.Axora.MVP.security.dto.LoginRequest;
import com.example.Axora.MVP.security.dto.SignUpRequest;
import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.DuplicatedUserInfoException;
import com.example.Axora.MVP.user.Service.AccountService;
import com.example.Axora.MVP.security.Service.RefreshTokenService;
import com.example.Axora.MVP.user.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
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
    public AuthResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        // Authenticate and generate access token
        String accessToken = authenticateAndGetToken(
                loginRequest.email(),
                loginRequest.password()
        );

        Account account = accountService.findByEmail(loginRequest.email());

        // Generate refresh token
        String refreshToken = tokenProvider.generateRefreshToken(account.getId().toString());

        // Save login session
        refreshTokenService.createSession(
                account,
                refreshToken,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return new AuthResponse(accessToken, refreshToken);
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
        accountService.saveAccount(account);

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
}
