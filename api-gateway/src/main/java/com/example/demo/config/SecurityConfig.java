package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

		return http.csrf(CsrfSpec::disable)
				.authorizeExchange(
						exchanges -> exchanges
								.pathMatchers("/account/**", "/friendship/**", "/article/**")
								.authenticated()
								.anyExchange().permitAll())
				.cors(cors -> cors.configurationSource(request -> config))
				// .cors().disable()
				.build();
	}
}
