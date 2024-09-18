package com.taskmanager.taskervice.task_manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Check if the token is blacklisted
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
