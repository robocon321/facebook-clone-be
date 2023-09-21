package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.exception.CredentialException;
import com.example.demo.exception.JwtTokenException;
import com.example.demo.exception.ResourceCreationException;

@ControllerAdvice
public class AuthHandler {
	
    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceCreationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
	@ExceptionHandler(BindException.class)
	public ResponseEntity<Object> argumentNotValidMultipartExceptionHandler(BindException ex, WebRequest request) {
		String message = "";
		for (FieldError error : ex.getFieldErrors()) {
			message += error.getDefaultMessage() + ". \n";
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<Object> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<Object> handleCredentialException(CredentialException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
