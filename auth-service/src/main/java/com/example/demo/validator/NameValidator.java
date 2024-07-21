package com.example.demo.validator;

import com.example.demo.annotation.ValidName;
import com.example.demo.util.ValidatePattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return false;
        }
        return name.matches(ValidatePattern.NAME);
    }

}
