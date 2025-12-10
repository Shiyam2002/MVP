package com.example.Axora.MVP.user.Service.account;

import com.example.Axora.MVP.security.Entity.Role;
import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Exception.Account.AccountAlreadyExistException;
import com.example.Axora.MVP.user.Exception.Account.AccountNotFoundException;
import com.example.Axora.MVP.user.Repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account with email %s not found".formatted(email)
                ));
    }

    @Override
    public Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public boolean hasAccountWithEmail(String email) {
        return accountRepository.existsByEmail(email.trim().toLowerCase());
    }


    @Override
    @Transactional
    public Account createAccount(Account account) {

        // Normalize email
        if (account.getEmail() != null) {
            account.setEmail(account.getEmail().trim().toLowerCase());
        }

        // Normalize phone
        if (account.getPhone() != null && !account.getPhone().isBlank()) {
            account.setPhone(normalizePhone(account.getPhone()));
        }

        // Encode password if raw
        String rawPassword = account.getPasswordHash();
        if (rawPassword != null && !isBCryptHash(rawPassword)) {
            account.setPasswordHash(passwordEncoder.encode(rawPassword));
        }

        // Defaults
        if (account.getStatus() == null) account.setStatus("active");
        if (account.getEmailVerified() == null) account.setEmailVerified(false);
        if (account.getPhoneVerified() == null) account.setPhoneVerified(false);
        if (account.getAuthProvider() == null) account.setAuthProvider("LOCAL");
        if (account.getRefreshTokenVersion() == null) account.setRefreshTokenVersion(0);

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccount(Account incoming) {

        Account existing = findById(incoming.getId());

        // Email update
        if (incoming.getEmail() != null) {
            String normalized = incoming.getEmail().trim().toLowerCase();

            if (!normalized.equals(existing.getEmail())
                    && accountRepository.existsByEmail(normalized)) {
                throw new AccountAlreadyExistException("Email already in use");
            }

            existing.setEmail(normalized);
        }

        // Phone update
        if (incoming.getPhone() != null) {
            if (incoming.getPhone().isBlank()) {
                existing.setPhone(null);
                existing.setPhoneVerified(false);
            } else {
                existing.setPhone(normalizePhone(incoming.getPhone()));
                existing.setPhoneVerified(false);
            }
        }

        // Password update only if provided
        if (incoming.getPasswordHash() != null && !incoming.getPasswordHash().isBlank()) {
            String raw = incoming.getPasswordHash();
            existing.setPasswordHash(
                    isBCryptHash(raw) ? raw : passwordEncoder.encode(raw)
            );
        }

        // Status
        if (incoming.getStatus() != null) {
            existing.setStatus(incoming.getStatus());
        }

        // Provider fields
        if (incoming.getAuthProvider() != null) {
            existing.setAuthProvider(incoming.getAuthProvider());
        }
        if (incoming.getProviderId() != null) {
            existing.setProviderId(incoming.getProviderId());
        }

        // Verification flags
        if (incoming.getEmailVerified() != null) {
            existing.setEmailVerified(incoming.getEmailVerified());
        }

        if (incoming.getPhoneVerified() != null) {
            existing.setPhoneVerified(incoming.getPhoneVerified());
        }

        // Refresh token version
        if (incoming.getRefreshTokenVersion() != null) {
            existing.setRefreshTokenVersion(incoming.getRefreshTokenVersion());
        }

        return accountRepository.save(existing);
    }


    @Override
    @Transactional
    public Account updateEmail(UUID accountId, String newEmail) {
        Account acc = findById(accountId);

        newEmail = newEmail.trim().toLowerCase();

        if (!newEmail.equals(acc.getEmail()) && accountRepository.existsByEmail(newEmail)) {
            throw new AccountAlreadyExistException("Email already in use");
        }

        acc.setEmail(newEmail);
        acc.setEmailVerified(false);  // MUST reverify email
        return accountRepository.save(acc);
    }

    @Override
    @Transactional
    public void updatePassword(UUID accountId, String oldPassword, String newPassword) {
        Account acc = findById(accountId);

        if (!passwordEncoder.matches(oldPassword, acc.getPasswordHash())) {
            throw new RuntimeException("Old password does not match");
        }

        acc.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
    }


    @Override
    @Transactional
    public Account updatePhone(UUID id, String phone) {
        Account acc = findById(id);

        if (phone == null || phone.isBlank()) {
            acc.setPhone(null);
            acc.setPhoneVerified(false);
        } else {
            acc.setPhone(normalizePhone(phone));
            acc.setPhoneVerified(false);
        }

        return accountRepository.save(acc);
    }


    @Override
    @Transactional
    public void disableAccount(UUID accountId) {
        Account acc = findById(accountId);
        acc.setStatus("disabled");
        accountRepository.save(acc);
    }

    @Override
    @Transactional
    public void enableAccount(UUID accountId) {
        Account acc = findById(accountId);
        acc.setStatus("active");
        accountRepository.save(acc);
    }


    @Override
    @Transactional
    public void assignRole(UUID accountId, Role role) {
        Account acc = findById(accountId);
        acc.getRoles().add(role);
        accountRepository.save(acc);
    }

    @Override
    @Transactional
    public void removeRole(UUID accountId, Role role) {
        Account acc = findById(accountId);
        acc.getRoles().remove(role);
        accountRepository.save(acc);
    }


    @Override
    @Transactional
    public void invalidateAllSessions(UUID accountId) {
        Account acc = findById(accountId);

        acc.setRefreshTokenVersion(acc.getRefreshTokenVersion() + 1);
        acc.setLastLoginAt(new Timestamp(System.currentTimeMillis()));

        accountRepository.save(acc);
    }


    // --------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------

    private boolean isBCryptHash(String password) {
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }

    private String normalizePhone(String phone) {
        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        // Add your country-specific logic here
        if (!cleaned.startsWith("+")) {
            cleaned = "+91" + cleaned;
        }
        return cleaned;
    }
}
