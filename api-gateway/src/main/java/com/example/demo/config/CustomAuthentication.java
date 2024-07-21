package com.example.demo.config;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthentication extends UsernamePasswordAuthenticationToken {
    public CustomAuthentication(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    // Add custom fields and methods as needed
}
