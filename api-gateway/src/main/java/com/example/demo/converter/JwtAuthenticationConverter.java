package com.example.demo.converter;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.SecurityContextServerWebExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.config.CustomUserDetailService;
import com.example.demo.utils.Const;
import com.example.demo.utils.ServerHttpRequestDecoratorUtils;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {
	private final CustomUserDetailService customUserDetailService;
	private final WebClient webClient;

	private static final String authUrl = "http://localhost:9002/api/v1/auth";

	public JwtAuthenticationConverter(
			CustomUserDetailService customUserDetailService,
			WebClient.Builder webClientBuilder) {
		this.customUserDetailService = customUserDetailService;
		this.webClient = webClientBuilder.baseUrl(authUrl).build();
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		if (isWebsocketProtocol(exchange)) {
			String uri = exchange.getRequest().getURI().toString();
			Map<String, String> queryParams = UriComponentsBuilder.fromUriString(uri)
					.build()
					.getQueryParams()
					.toSingleValueMap();
			if (queryParams.containsKey("token")) {
				String jwtToken = queryParams.get("token");
				return Mono.just(exchange)
						.flatMap(exch -> getUserId(exchange, jwtToken));
			} else {
				ServerHttpRequestDecoratorUtils.removeHeaderItem(
						exchange,
						Const.X_USER_ID_HEADER);
				return Mono.empty();
			}
		}

		if (exchange instanceof SecurityContextServerWebExchange) {
			return Mono.empty();
		}

		return Mono.just(exchange)
				.flatMap(exch -> {
					if (exch.getRequest().getHeaders().containsKey("Authorization")) {
						Object token = exch.getRequest().getHeaders().get("Authorization");
						if (token != null && !token.toString().startsWith("Bearer ")) {
							String jwt = token.toString().substring(7).trim();
							return getUserId(exch, jwt);
						}
						return Mono.empty();
					}
					return Mono.empty();
				});
	}

	private boolean isWebsocketProtocol(ServerWebExchange exchange) {
		String key = "Upgrade";
		List<String> values = exchange.getRequest().getHeaders().get(key);
		return values != null && !values.isEmpty() && "websocket".equalsIgnoreCase(values.get(0));
	}

	private Mono<UsernamePasswordAuthenticationToken> getUserId(ServerWebExchange exch, String jwtToken) {
		return webClient.post()
				.uri("/user-id")
				.bodyValue(jwtToken)
				.retrieve()
				.bodyToMono(Integer.class)
				.flatMap(userId -> {
					if (userId == null) {
						ServerHttpRequestDecoratorUtils.removeHeaderItem(
								exch,
								Const.X_USER_ID_HEADER);
						return Mono.empty();
					}
					UserDetails userDetails = customUserDetailService
							.loadUserById((Integer) userId);
					exch.getRequest().mutate().header(Const.X_USER_ID_HEADER, userId.toString());
					return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities()));
				})
				.onErrorResume(ex -> {
					ServerHttpRequestDecoratorUtils.removeHeaderItem(
							exch,
							Const.X_USER_ID_HEADER);
					return Mono.empty();
				});
	}
}
