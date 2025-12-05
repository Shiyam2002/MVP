package com.example.Axora.MVP.user.Service;

import com.example.Axora.MVP.user.Entity.Account;
import com.example.Axora.MVP.user.Exception.AccountNotFoundException;
import com.example.Axora.MVP.user.Repository.AccountRepository;
import com.example.Axora.MVP.user.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account with email %s not found".formatted(email)));
    }

    @Override
    public Account saveAccount(Account account) {
        // Encode raw password BEFORE saving
        account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));
        return accountRepository.save(account);
    }

    @Override
    public boolean hasAccountWithEmail(String email) {
        return accountRepository.existsByEmail(email);
    }
}

