package com.example.demo.exception;

import com.example.demo.type.ErrorCodeType;

public class ResourceCreationException extends RuntimeException {
	public ResourceCreationException(ErrorCodeType code) {
		super(code.getMessage());
	}
}
