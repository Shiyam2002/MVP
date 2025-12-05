package com.example.Axora.MVP.user.Exception;

public class AccountNotFoundException extends RuntimeException  {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
