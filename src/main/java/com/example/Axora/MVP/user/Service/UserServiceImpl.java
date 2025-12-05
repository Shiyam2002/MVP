package com.example.Axora.MVP.user.Service;

import com.example.Axora.MVP.security.Entity.Role;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Repository.RoleRepository;
import com.example.Axora.MVP.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // Inject from SecurityConfig
    private final RoleRepository roleRepository;


    @Override
    public boolean hasUserWithUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        // ALWAYS encode password here, not in controller
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role defaultRole = roleRepository.findByName("ROLE_USER");
        user.getRoles().add(defaultRole);

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}

