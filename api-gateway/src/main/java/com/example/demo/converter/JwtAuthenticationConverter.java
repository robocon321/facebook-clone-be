package com.example.demo.converter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.example.demo.config.CustomUserDetailService;
import com.example.demo.provider.JwtProvider;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {
	private JwtProvider tokenProvider;
	private CustomUserDetailService customUserDetailService;

	public JwtAuthenticationConverter(JwtProvider tokenProvider, CustomUserDetailService customUserDetailService) {
		this.tokenProvider = tokenProvider;
		this.customUserDetailService = customUserDetailService;
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		String jwt = extractTokenFromRequest(exchange);
		if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
			Integer userId = tokenProvider.getAccountIdFromJWT(jwt);
			UserDetails userDetails = customUserDetailService.loadUserById(userId);
			if (jwt != null) {
				return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities()));
			}
		}
		return Mono.empty();
	}

	private static String extractTokenFromRequest(ServerWebExchange exchange) {
		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}
}
