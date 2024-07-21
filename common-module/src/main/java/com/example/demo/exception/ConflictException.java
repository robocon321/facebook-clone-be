package com.example.demo.exception;

import java.text.MessageFormat;

import com.example.demo.type.ErrorCodeType;

public class ConflictException extends RuntimeException {

    public ConflictException(ErrorCodeType code) {
        super(code.getMessage());
    }

    public ConflictException(ErrorCodeType code, Object... params) {
        super(MessageFormat.format(code.getMessage(), params));
    }

}
