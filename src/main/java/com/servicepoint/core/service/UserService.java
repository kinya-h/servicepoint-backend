package com.servicepoint.core.service;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse createUser(RegisterRequest request ,   HttpServletRequest httpRequest) throws Exception;
    LoginResponse loginUser(LoginRequest request, HttpServletRequest httpRequest);
    UserResponse updateProfile(UpdateProfileRequest request, int  userId);
    UserResponse getUserById(Integer userId);
    List<User> findAllUsers();
    Optional<User> findUserById(Integer userId);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Integer userId);
}