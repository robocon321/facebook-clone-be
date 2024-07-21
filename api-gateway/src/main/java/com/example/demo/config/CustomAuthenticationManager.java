package com.example.demo.config;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication.isAuthenticated()) {
			return Mono.just(
					new CustomAuthentication(authentication.getPrincipal(), null, authentication.getAuthorities()));
		} else
			return Mono.empty();
	}
}
