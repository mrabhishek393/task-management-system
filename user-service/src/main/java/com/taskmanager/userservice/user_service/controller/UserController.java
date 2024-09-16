package com.taskmanager.userservice.user_service.controller;

import com.taskmanager.userservice.user_service.model.UserRegistrationRequest;
import com.taskmanager.userservice.user_service.entity.User;
import com.taskmanager.userservice.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            // Use the role from the request if provided, otherwise default to ROLE_USER
            String role = request.getRole() != null ? request.getRole() : "ROLE_USER";
            User registeredUser = userService.registerUserWithRole(user, role);

            return ResponseEntity.ok(registeredUser);

    }
}

