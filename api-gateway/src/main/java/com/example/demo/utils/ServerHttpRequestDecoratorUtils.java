package com.example.demo.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;

public class ServerHttpRequestDecoratorUtils {
    public static ServerWebExchange removeHeaderItem(ServerWebExchange exchange, String headerName) {
        ServerHttpRequest request = exchange.getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        headers.remove(headerName);

        ServerHttpRequest newRequest = new ServerHttpRequestDecorator(request) {
            @SuppressWarnings("null")
            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };

        return exchange.mutate().request(newRequest).build();
    }
}
