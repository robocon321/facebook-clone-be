package com.example.demo.exception;

public class ResourceCreationException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceCreationException(String message) {
        super(message);
    }
}
