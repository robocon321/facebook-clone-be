package com.example.demo.exception;

import java.text.MessageFormat;

import com.example.demo.type.ErrorCodeType;

public class NotFoundException extends RuntimeException {
	public NotFoundException(ErrorCodeType code) {
		super(code.getMessage());
	}

	public NotFoundException(ErrorCodeType code, Object... params) {
		super(MessageFormat.format(code.getMessage(), params));
	}
}
