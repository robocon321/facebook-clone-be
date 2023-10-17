package com.example.demo.exception;


public class AuthorizeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthorizeException(String message) {
        super(message);
    }
}
