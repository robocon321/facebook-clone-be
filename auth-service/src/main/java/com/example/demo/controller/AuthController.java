package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.request.CreateAccountRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.CreateAccountResponse;
import com.example.demo.exception.AuthorizeException;
import com.example.demo.exception.BlockException;
import com.example.demo.service.AuthService;
import com.example.demo.utils.Const;

import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService service;
    private AuthenticationManager authenticationManager;

    public AuthController(AuthService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateAccountResponse> addNewAccount(@Valid @RequestBody CreateAccountRequest request) {
        try {
            CreateAccountResponse response = service.saveAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/token")
    public String getToken(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            if (authenticate.isAuthenticated()) {
                return service.generateToken((CustomUserDetails) authenticate.getPrincipal());
            } else {
                throw new BadRequestException("Invalid access");
            }
        } catch (BlockException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthorizeException("Username or password is incorrect");
        }
    }

    @PostMapping("/user-id")
    public Integer getUserIdFromToken(@RequestBody String jwt) {
        if (jwt != null) {
            return service.getUserIdFromJWT(jwt);
        } else {
            return null;
        }
    }

    @GetMapping
    public String welcome() {
        return "Welcome to auth-service";
    }
}
