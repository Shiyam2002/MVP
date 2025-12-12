package com.example.Axora.MVP.user.Mapper.Account;

import com.example.Axora.MVP.user.DTO.Account.AccountResponse;
import com.example.Axora.MVP.user.DTO.Account.UpdateAccountRequest;
import com.example.Axora.MVP.user.DTO.Account.UpdateEmailRequest;
import com.example.Axora.MVP.user.DTO.Account.UpdatePhoneRequest;
import com.example.Axora.MVP.user.Entity.Account;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountResponse toResponse(Account account) {
        if(account == null) return null;

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

    @Override
    public Account toPatchEntity(UUID id, UpdateAccountRequest request) {
        if(request == null) return null;

        Account account = new Account();
        account.setId(id);
        account.setEmail(request.email());
        account.setPhone(request.phone());
        account.setEmailVerified(request.emailVerified());
        account.setPhoneVerified(request.phoneVerified());
        account.setAuthProvider(request.authProvider());
        account.setProviderId(request.providerId());

        return account;
    }

    @Override
    public Account toPatchEmail(UUID id, UpdateEmailRequest request) {
        Account account = new Account();
        account.setId(id);
        account.setEmail(request.newEmail());
        return account;
    }

    @Override
    public Account toPatchPhone(UUID id, UpdatePhoneRequest request) {
       Account account = new Account();
       account.setId(id);
       account.setPhone(request.phone());
       return account;
    }
}
