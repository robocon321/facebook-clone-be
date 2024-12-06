package com.example.demo.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class UserSessionManager {
    private final ConcurrentMap<String, Integer> sessionUserMap;

    public UserSessionManager() {
        this.sessionUserMap = new ConcurrentHashMap<>();
    }

    public void addSession(String sessionId, Integer userId) {
        sessionUserMap.put(sessionId, userId);
    }

    public void removeSession(String sessionId) {
        sessionUserMap.remove(sessionId);
    }

    public Integer getUserId(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    public boolean hasSession(String sessionId) {
        return sessionUserMap.containsKey(sessionId);
    }

    public ConcurrentMap<String, Integer> getAllSession() {
        return sessionUserMap;
    }
}