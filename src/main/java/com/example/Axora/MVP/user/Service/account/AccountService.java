package com.example.Axora.MVP.user.Service.account;

import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.security.Entity.Role;

import java.util.UUID;

public interface AccountService {

    // Create
    Account createAccount(Account account);

    // Update (PATCH-style)
    Account updateAccount(Account partialUpdate);

    // Update individual fields
    Account updateEmail(UUID accountId, String newEmail);
    void updatePassword(UUID accountId, String oldPassword, String newPassword);
    Account updatePhone(UUID accountId, String phone);

    // Activation / Status
    void disableAccount(UUID accountId);
    void enableAccount(UUID accountId);

    // RBAC Role Management
    void assignRole(UUID accountId, Role role);
    void removeRole(UUID accountId, Role role);

    // Sessions
    void invalidateAllSessions(UUID accountId);

    // Lookups
    Account findByEmail(String email);
    Account findById(UUID id);
    boolean hasAccountWithEmail(String email);
}
