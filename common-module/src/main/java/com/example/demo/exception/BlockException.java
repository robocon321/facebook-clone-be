package com.example.demo.exception;

import java.text.MessageFormat;

import com.example.demo.type.ErrorCodeType;

public class BlockException extends RuntimeException {
	public BlockException(ErrorCodeType code) {
		super(code.getMessage());
	}

	public BlockException(ErrorCodeType code, Object... params) {
		super(MessageFormat.format(code.getMessage(), params));
	}
}
