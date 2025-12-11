package com.example.Axora.MVP.user.Route.V1;

import com.example.Axora.MVP.security.Entity.Role;
import com.example.Axora.MVP.security.Repository.RoleRepository;
import com.example.Axora.MVP.user.DTO.Account.*;
import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/me")
    public AccountResponse getMyAccount(Authentication auth){
        Account account = accountService.findByEmail(auth.getName());

        return new AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getPhone(),
                account.getStatus(),
                account.getEmailVerified(),
                account.getPhoneVerified(),
                account.getAuthProvider(),
                account.getRoles()
        );
    }

    @PatchMapping("/{id}")
    public AccountResponse updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request
            ){
        Account update = new Account();
        update.setId(id);
        update.setEmail(request.email());
        update.setPhone(request.phone());
        update.setEmailVerified(request.emailVerified());
        update.setPhoneVerified(request.phoneVerified());
        update.setProviderId(request.providerId());
        update.setAuthProvider(request.authProvider());

        Account updated = accountService.updateAccount(update);

        return new AccountResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getPhone(),
                updated.getStatus(),
                updated.getEmailVerified(),
                updated.getPhoneVerified(),
                updated.getAuthProvider(),
                updated.getRoles()
        );
    }

    @PatchMapping("/{id}/email")
    public AccountResponse updateEmail(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmailRequest request
            ){
        Account updated = accountService.updateEmail(id, request.newEmail());

        return new AccountResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getPhone(),
                updated.getStatus(),
                updated.getEmailVerified(),
                updated.getPhoneVerified(),
                updated.getAuthProvider(),
                updated.getRoles()
        );
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePasswordRequest request
            ){
        accountService.updatePassword(id, request.oldPassword(), request.newPassword());
    }

    public AccountResponse updatePhone(
            @PathVariable UUID id,
            @RequestBody UpdatePhoneRequest request
    ){
        Account updated = accountService.updatePhone(id, request.phone());

        return new AccountResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getPhone(),
                updated.getStatus(),
                updated.getEmailVerified(),
                updated.getPhoneVerified(),
                updated.getAuthProvider(),
                updated.getRoles()
        );
    }

    @PostMapping("/{id}/roles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignRole(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRoleRequest request
    ){
        Role role = roleRepository.findByName(request.roleName())
                .orElseThrow(() -> new RuntimeException("Role not Found"));

        accountService.assignRole(id, role);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRole(
            @PathVariable UUID id,
            @PathVariable String roleName
    ) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        accountService.removeRole(id, role);
    }

    @PostMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable UUID id) {
        accountService.disableAccount(id);
    }

    @PostMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable UUID id) {
        accountService.enableAccount(id);
    }

    @PostMapping("/{id}/logout-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutAllDevices(@PathVariable UUID id) {
        accountService.invalidateAllSessions(id);
    }


}
