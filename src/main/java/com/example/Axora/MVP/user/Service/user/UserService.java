package com.example.Axora.MVP.user.Service.user;

import com.example.Axora.MVP.user.Entity.User;

public interface UserService {

    boolean hasUserWithUsername(String username);

    User saveUser(User user);

    User findByUsername(String username);
}
