package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.demo.converter.JwtAuthenticationConverter;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.repository.AccountRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.csrf().disable()
				.authorizeExchange(exchanges -> exchanges.pathMatchers("/account/**").authenticated()
						.anyExchange().permitAll()
						.and()
						.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION))
				.build();

	}
	
	@Autowired
	public JwtAuthenticationFilter jwtAuthenticationFilter;

}
