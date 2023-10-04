package com.example.demo.exception;

public class ConflictAccountException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ConflictAccountException(String message) {
        super(message);
    }

}
