package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.exception.AuthorizeException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BlockException;

@ControllerAdvice
public class CommonExceptionHandler {
	

    @ExceptionHandler(BlockException.class)
    public ResponseEntity<Object> handleBlockException(BlockException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Object> handleArgumentNotValidException(BindException ex, WebRequest request) {
		String message = "";
		for (FieldError error : ex.getFieldErrors()) {
			message += error.getDefaultMessage() + ". \n";
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity<Object> handleAuthorizeException(AuthorizeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}
