package com.example.Axora.MVP.security.Route.V1;

import com.example.Axora.MVP.security.SecurityConfig;
import com.example.Axora.MVP.security.TokenProvider;
import com.example.Axora.MVP.security.dto.AuthResponse;
import com.example.Axora.MVP.security.dto.LoginRequest;
import com.example.Axora.MVP.security.dto.SignUpRequest;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.DuplicatedUserInfoException;
import com.example.Axora.MVP.user.Service.RefreshTokenService;
import com.example.Axora.MVP.user.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/authenticate")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        String accessToken = authenticateAndGetToken(loginRequest.username(), loginRequest.password());

        User user = userService.findByUsername(loginRequest.username());

        String refreshToken = tokenProvider.generateRefreshToken(user.getId().toString());

        refreshTokenService.createSession(
                user,
                refreshToken,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return new AuthResponse(accessToken, refreshToken);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthResponse signUp(
            @Valid @RequestBody SignUpRequest signUpRequest,
            HttpServletRequest request
    ) {
        // ---- Check duplicates ----
        if (userService.hasUserWithUsername(signUpRequest.username())) {
            throw new DuplicatedUserInfoException(
                    "Username %s already been used".formatted(signUpRequest.username())
            );
        }
        if (userService.hasUserWithEmail(signUpRequest.email())) {
            throw new DuplicatedUserInfoException(
                    "Email %s already been used".formatted(signUpRequest.email())
            );
        }

        // ---- Create and save user ----
        User user = mapSignUpRequestToUser(signUpRequest);
        userService.saveUser(user);

        // ---- Authenticate newly created user ----
        String accessToken = authenticateAndGetToken(signUpRequest.username(), signUpRequest.password());

        // ---- Generate refresh token ----
        String refreshToken = tokenProvider.generateRefreshToken(user.getId().toString());

        // ---- Save session ----
        refreshTokenService.createSession(
                user,
                refreshToken,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        // ---- Return BOTH tokens ----
        return new AuthResponse(accessToken, refreshToken);
    }


    private String authenticateAndGetToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return tokenProvider.generateAccessToken(authentication);
    }

    private User mapSignUpRequestToUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(signUpRequest.username());
        user.setPassword(signUpRequest.password());  // RAW password, will be encoded by service
        user.setEmail(signUpRequest.email());
        user.setEmailVerified(true);
        return user;
    }
}
