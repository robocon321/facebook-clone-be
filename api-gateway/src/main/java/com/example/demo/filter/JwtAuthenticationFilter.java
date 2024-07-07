package com.example.demo.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;

import com.example.demo.config.CustomAuthenticationManager;
import com.example.demo.converter.JwtAuthenticationConverter;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
	public JwtAuthenticationFilter(@Autowired CustomAuthenticationManager authenticationManager,
			@Autowired JwtAuthenticationConverter jwtAuthenticationConverter) {
		super(authenticationManager);
		setServerAuthenticationConverter(jwtAuthenticationConverter);
	}
}
