package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.example.demo.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@SuppressWarnings("removal")
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

		return http.csrf().disable()
				.authorizeExchange(exchanges -> exchanges.pathMatchers("/account/**", "/friendship/**", "/post/**").authenticated()
						.anyExchange().permitAll()
						.and()
						.cors().configurationSource(request -> config)
						.and()
//						.cors().disable()
						.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION))
				.build();

	}
	
	@Autowired
	public JwtAuthenticationFilter jwtAuthenticationFilter;
}
