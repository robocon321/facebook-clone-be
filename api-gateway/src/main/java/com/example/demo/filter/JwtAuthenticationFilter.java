package com.example.demo.filter;

import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;

import com.example.demo.config.CustomAuthenticationManager;
import com.example.demo.converter.JwtAuthenticationConverter;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
	public JwtAuthenticationFilter(CustomAuthenticationManager authenticationManager,
			JwtAuthenticationConverter jwtAuthenticationConverter) {
		super(authenticationManager);
		setServerAuthenticationConverter(jwtAuthenticationConverter);
	}
}
