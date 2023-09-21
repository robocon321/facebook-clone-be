package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.JwtTokenException;

@ControllerAdvice
public class AuthHandler {
    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<Object> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
