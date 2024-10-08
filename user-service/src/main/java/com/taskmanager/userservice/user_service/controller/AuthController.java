package com.taskmanager.userservice.user_service.controller;

import com.taskmanager.userservice.user_service.model.AuthenticationRequest;
import com.taskmanager.userservice.user_service.security.TokenBlacklist;
import com.taskmanager.userservice.user_service.service.AuthenticationService;
import com.taskmanager.userservice.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        String jwt = authenticationService.authenticateAndGenerateToken(authenticationRequest);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            long expirationTime = jwtUtil.getExpirationTime(jwtToken) - System.currentTimeMillis();
            tokenBlacklist.addToBlacklist(jwtToken, expirationTime);
            return ResponseEntity.ok().body("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");

    }
}
