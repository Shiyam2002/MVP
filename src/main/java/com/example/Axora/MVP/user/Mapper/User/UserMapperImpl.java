package com.example.Axora.MVP.user.Mapper.User;

import com.example.Axora.MVP.user.DTO.User.CreateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UpdateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UserResponse;
import com.example.Axora.MVP.user.Entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getTimezone()
        );
    }

    @Override
    public User toCreateEntity(CreateUserRequest req) {
        User user = new User();
        user.setUsername(req.username());
        user.setAvatarUrl(req.avatarUrl());
        user.setBio(req.bio());
        user.setTimezone(req.timezone());
        return user;
    }

    @Override
    public User toPatchEntity(UUID id, UpdateUserRequest req) {
        User user = new User();
        user.setId(id);
        user.setUsername(req.username());
        user.setAvatarUrl(req.avatarurl());
        user.setBio(req.bio());
        user.setTimezone(req.timezone());
        return user;
    }
}

