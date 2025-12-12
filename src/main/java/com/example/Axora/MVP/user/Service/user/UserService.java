package com.example.Axora.MVP.user.Service.user;

import com.example.Axora.MVP.user.Entity.User;

import java.util.UUID;

public interface UserService {

    boolean hasUserWithUsername(String username);

    User saveUser(User user);

    User findByUsername(String username);

    User createUser(User user);

    User updateUser(User user);

    User findById(UUID id);

    boolean existingUsername(String username);
}
