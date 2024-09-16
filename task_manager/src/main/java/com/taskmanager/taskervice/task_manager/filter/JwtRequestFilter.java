package com.taskmanager.taskervice.task_manager.filter;

import com.taskmanager.taskervice.task_manager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        Long userId = null;
        String jwt = null;
        List<String> roles = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            userId = jwtUtil.extractUserId(jwt);
            roles = jwtUtil.extractRoles(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                CustomAuthenticationDetails customDetails = new CustomAuthenticationDetails(userId, webAuthenticationDetails);

                authenticationToken.setDetails(customDetails);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}