package com.taskmanager.userservice.user_service.security;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Set;

@Component
public class TokenBlacklist {
    private Set<String> blacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}