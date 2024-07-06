package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.exception.BlockException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BlockException.class)
    public ResponseEntity<Object> handleBlockException(BlockException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleArgumentNotValidException(BindException ex, WebRequest request) {
        String message = "";
        for (FieldError error : ex.getFieldErrors()) {
            message += error.getDefaultMessage() + ". \n";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
