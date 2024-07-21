package com.example.demo.exception;

import java.text.MessageFormat;

import com.example.demo.type.ErrorCodeType;

public class BadRequestException extends RuntimeException {
	public BadRequestException(ErrorCodeType code) {
		super(code.getMessage());
	}

	public BadRequestException(ErrorCodeType code, Object... params) {
		super(MessageFormat.format(code.getMessage(), params));
	}
}
