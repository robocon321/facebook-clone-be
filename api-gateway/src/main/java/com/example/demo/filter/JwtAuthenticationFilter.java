package com.example.demo.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;

import com.example.demo.config.CustomAuthenticationManager;
import com.example.demo.config.CustomUserDetailService;
import com.example.demo.converter.JwtAuthenticationConverter;
import com.example.demo.provider.JwtProvider;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
	public JwtAuthenticationFilter(@Autowired CustomAuthenticationManager authenticationManager, @Autowired JwtAuthenticationConverter jwtAuthenticationConverter) {		
		super(authenticationManager);		
		setServerAuthenticationConverter(jwtAuthenticationConverter);
	}

}
