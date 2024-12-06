package com.example.demo.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.demo.utils.Const;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @SuppressWarnings("null")
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String userId = request.getHeaders().getFirst(Const.X_USER_ID_HEADER);
        if (userId != null) {
            attributes.put("userId", userId);
            return true;
        }
        return false;
    }

    @SuppressWarnings("null")
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }
}
