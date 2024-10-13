package com.example.demo.converter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.demo.config.CustomUserDetailService;
import com.example.demo.utils.Const;

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
		return Mono.just(exchange)
				.flatMap(exch -> {
					if (exch.getRequest().getHeaders().containsKey("Authorization")) {
						Object token = exch.getRequest().getHeaders().get("Authorization");
						if (token != null && !token.toString().startsWith("Bearer ")) {
							String jwt = token.toString().substring(7).trim();
							return webClient.post()
									.uri("/user-id")
									.bodyValue(jwt)
									.retrieve()
									.bodyToMono(Integer.class)
									.flatMap(userId -> {
										if (userId == null) {
											exch.getRequest().getHeaders().remove(Const.X_USER_ID_HEADER);
											return Mono.empty();
										}
										UserDetails userDetails = customUserDetailService
												.loadUserById((Integer) userId);
										exch.getRequest().mutate().header(Const.X_USER_ID_HEADER, userId.toString());
										return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null,
												userDetails.getAuthorities()));
									})
									.onErrorResume(ex -> {
										exch.getRequest().getHeaders().remove(Const.X_USER_ID_HEADER);
										return Mono.empty();
									});
						}
						return Mono.empty();
					}
					return Mono.empty();
				});
	}
}
