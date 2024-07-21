package com.example.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.exception.AuthorizeException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;

@ControllerAdvice
public class CommonExceptionHandler {

    /**
     * @param ex
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(Throwable ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BlockException.class)
    public ResponseEntity<Object> handleBlockException(Throwable ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(Throwable ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleArgumentNotValidException(Errors ex, WebRequest request) {
        StringBuilder message = new StringBuilder("");
        for (FieldError error : ex.getFieldErrors()) {
            message.append(error.getDefaultMessage()).append(". \n");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message.toString().trim());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(Throwable ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity<Object> handleAuthorizeException(Throwable ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}
