package com.example.Axora.MVP.user.Service;

import com.example.Axora.MVP.user.Entity.User;

public interface UserService {
    boolean hasUserWithUsername(String username);
    boolean hasUserWithEmail(String email);
    User saveUser(User user);
    User findByUsername(String username);
}