package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.exception.BlockAccountException;
import com.example.demo.exception.ConflictAccountException;
import com.example.demo.exception.NotFoundAccountException;

@ControllerAdvice
public class AccountHandler {
	
    @ExceptionHandler(NotFoundAccountException.class)
    public ResponseEntity<Object> handleNotFoundAccountException(NotFoundAccountException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BlockAccountException.class)
    public ResponseEntity<Object> handleBlockAccountException(BlockAccountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ConflictAccountException.class)
    public ResponseEntity<Object> handleConflictAccountException(ConflictAccountException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Object> argumentNotValidMultipartExceptionHandler(BindException ex, WebRequest request) {
		String message = "";
		for (FieldError error : ex.getFieldErrors()) {
			message += error.getDefaultMessage() + ". \n";
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}
}
