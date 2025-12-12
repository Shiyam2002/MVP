package com.example.Axora.MVP.user.Mapper.Account;

import com.example.Axora.MVP.user.DTO.Account.*;
import com.example.Axora.MVP.user.Entity.Account;

import java.util.UUID;

public interface AccountMapper {
    AccountResponse toResponse(Account account);
    Account toPatchEntity(UUID id, UpdateAccountRequest request);
    Account toPatchEmail(UUID id, UpdateEmailRequest request);
    Account toPatchPhone(UUID id, UpdatePhoneRequest request);
}
