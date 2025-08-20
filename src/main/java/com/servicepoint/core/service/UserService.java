package com.servicepoint.core.service;

import com.servicepoint.core.dto.LoginRequest;
import com.servicepoint.core.dto.LoginResponse;
import com.servicepoint.core.dto.RegisterRequest;
import com.servicepoint.core.dto.UserResponse;
import com.servicepoint.core.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse createUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request, HttpServletRequest httpRequest);
    UserResponse getUserById(Integer userId);
    List<User> findAllUsers();
    Optional<User> findUserById(Integer userId);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Integer userId);
}