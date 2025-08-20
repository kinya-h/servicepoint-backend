package com.servicepoint.core.controller;

import com.servicepoint.core.dto.UserResponse;
import com.servicepoint.core.model.User;
import com.servicepoint.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<User> userOptional = userService.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserResponse userDTO = userService.getUserById(userOptional.get().getUserId());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthController.ErrorResponse("Profile retrieval failed", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
        try {
            UserResponse userDTO = userService.getUserById(userId);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthController.ErrorResponse("User retrieval failed", e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userService.findUserById(#userId).orElse(new com.servicepoint.core.model.User()).email")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId, @RequestBody User user) {
        try {
            Optional<User> existingUser = userService.findUserById(userId);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            user.setUserId(userId);
            User updatedUser = userService.saveUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthController.ErrorResponse("User update failed", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthController.ErrorResponse("User deletion failed", e.getMessage()));
        }
    }
}