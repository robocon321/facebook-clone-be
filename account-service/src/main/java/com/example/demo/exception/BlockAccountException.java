package com.example.demo.exception;


public class BlockAccountException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BlockAccountException(String message) {
        super(message);
    }
}
