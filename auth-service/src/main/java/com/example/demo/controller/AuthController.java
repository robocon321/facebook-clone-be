package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.request.CreateAccountRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.CreateAccountResponse;
import com.example.demo.exception.CredentialException;
import com.example.demo.exception.JwtTokenException;
import com.example.demo.exception.ResourceCreationException;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<CreateAccountResponse> addNewAccount(@Valid @RequestBody CreateAccountRequest request) {
    	try {
        	CreateAccountResponse response = service.saveAccount(request);
        	return ResponseEntity.status(HttpStatus.CREATED).body(response);    		
    	} catch (RuntimeException e) {
    		e.printStackTrace();
    		throw new ResourceCreationException("Dupliation account email or phone");
    	}
    }

    @PostMapping("/token")
    public String getToken(@Valid @RequestBody LoginRequest request) {
    	try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));    		
            if (authenticate.isAuthenticated()) {
                return service.generateToken((CustomUserDetails) authenticate.getPrincipal()); 
            } else {
                throw new JwtTokenException("Invalid access");
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new CredentialException("Username or password is incorrect");
    	}
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }
    
    @GetMapping
    public String welcome() {
    	return "Hello world";
    }
}
