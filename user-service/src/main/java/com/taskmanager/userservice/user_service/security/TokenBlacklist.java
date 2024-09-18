package com.taskmanager.userservice.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private Set<String> blacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addToBlacklist(String token, long expirationTimeInSeconds) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
//        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}