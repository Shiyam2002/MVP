package com.example.Axora.MVP.user.Service.user;

import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Exception.User.UserAlreadyExistsException;
import com.example.Axora.MVP.user.Exception.User.UserNotFoundException;
import com.example.Axora.MVP.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean hasUserWithUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        // Nothing to encode here (password moved to Account)
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional
    public User createUser(User user) {

        if (user.getUsername() != null){
            user.setUsername(user.getUsername().trim());
        }

        if (userRepository.existsByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("User name %s already existing!".formatted(user.getUsername()));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {

        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (user.getUsername() != null) {
            String normalize = user.getUsername().trim();

            if (!normalize.equals(existing.getUsername())
                && userRepository.existsByUsername(normalize)){
                throw new UserAlreadyExistsException("Username Already exists!");
            }

            existing.setUsername(normalize);
        }

        if (user.getAvatarUrl() != null) {
            existing.setAvatarUrl(user.getAvatarUrl());
        }

        if (user.getBio() != null) {
            existing.setBio(user.getBio());
        }

        if (user.getTimezone() != null) {
            existing.setTimezone(user.getTimezone());
        }

        return userRepository.save(existing);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public boolean existingUsername(String username) {
        return userRepository.existsByUsername(username.trim());
    }
}
