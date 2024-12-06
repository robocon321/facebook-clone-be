package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.demo.interpretor.AuthHandshakeInterceptor;
import com.example.demo.interpretor.UserInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final UserInterceptor userInterceptor;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public WebSocketConfig(UserInterceptor userInterceptor, AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.userInterceptor = userInterceptor;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/article-topic");
        config.setApplicationDestinationPrefixes("/article-app", "/user");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/article")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:3000");
        registry
                .addEndpoint("/ws/article")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(userInterceptor);
    }
}
