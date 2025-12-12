package com.example.Axora.MVP.user.Mapper.User;

import com.example.Axora.MVP.user.DTO.User.CreateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UpdateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UserResponse;
import com.example.Axora.MVP.user.Entity.User;

import java.util.UUID;

public interface UserMapper {
    UserResponse toResponse(User user);
    User toCreateEntity(CreateUserRequest request);
    User toPatchEntity(UUID id, UpdateUserRequest req);
}
