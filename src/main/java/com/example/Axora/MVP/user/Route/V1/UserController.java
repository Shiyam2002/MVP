package com.example.Axora.MVP.user.Route.V1;

import com.example.Axora.MVP.user.DTO.User.CreateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UpdateUserRequest;
import com.example.Axora.MVP.user.DTO.User.UserResponse;
import com.example.Axora.MVP.user.Entity.User;
import com.example.Axora.MVP.user.Mapper.User.UserMapper;
import com.example.Axora.MVP.user.Service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id){
        User user = userService.findById(id);
        return userMapper.toResponse(user);
    }

    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
            ){
        User newUser = userMapper.toCreateEntity(request);
        User save = userService.createUser(newUser);
        return userMapper.toResponse(save);
    }


    @PatchMapping("{id}")
    public UserResponse updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
            ){
        User patch = userMapper.toPatchEntity(id, request);
        User updated = userService.updateUser(patch);

        return userMapper.toResponse(updated);
    }
}
