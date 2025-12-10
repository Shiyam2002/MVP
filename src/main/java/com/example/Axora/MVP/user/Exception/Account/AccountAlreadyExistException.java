package com.example.Axora.MVP.user.Exception.Account;

public class AccountAlreadyExistException extends RuntimeException  {
    public AccountAlreadyExistException(String message) {
        super(message);
    }
}
