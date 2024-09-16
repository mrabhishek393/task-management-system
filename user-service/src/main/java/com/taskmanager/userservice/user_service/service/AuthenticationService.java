package com.taskmanager.userservice.user_service.service;

import com.taskmanager.userservice.user_service.model.AuthenticationRequest;
import com.taskmanager.userservice.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String authenticateAndGenerateToken(AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        return jwtUtil.generateToken(userDetails);
    }
}
