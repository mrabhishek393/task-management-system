package com.taskmanager.userservice.user_service.service;

import com.taskmanager.userservice.user_service.entity.User;
import com.taskmanager.userservice.user_service.exception.ResourceNotFoundException;
import com.taskmanager.userservice.user_service.exception.UserAlreadyExistsException;
import com.taskmanager.userservice.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register a user and assign a role (e.g., ROLE_USER or ROLE_ADMIN)
    public User registerUserWithRole(User user, String role) {
        // Check if username or email already exists
        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.getRoles().add(role);
        // Ensure a non-null role is assigned (e.g., ROLE_USER or ROLE_ADMIN)
//        user.setRoles(Collections.singleton(role));  // Assign the specified role

        // Save the user to the database
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
