package com.example.demo.config;

import org.springframework.stereotype.Component;

@Component
public interface WebSocketSubscriber {
    String getDestination();

    void handleWebSocketSubcribe(String[] suffix, String sessionId);

    void handleWebSocketUnSubcribe(String[] suffix, String sessionId);
}
