package com.example.Axora.MVP.user.Service;

import com.example.Axora.MVP.user.Entity.Account;

public interface AccountService {

    Account findByEmail(String email);

    Account saveAccount(Account account);

    boolean hasAccountWithEmail(String email);
}
