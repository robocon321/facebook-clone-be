package com.example.demo.validator;

import com.example.demo.annotation.ValidPhone;
import com.example.demo.util.ValidatePattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    /**
     * @param phone
     * @param context
     * @return boolean
     */
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return false;
        }
        return phone.matches(ValidatePattern.PHONE);
    }
}
