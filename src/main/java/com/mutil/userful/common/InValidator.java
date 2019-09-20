package com.mutil.userful.common;

import com.mutil.userful.annotation.In;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InValidator  implements ConstraintValidator<In, String> {
    private String[] checkedValues;
    @Override
    public void initialize(In constraintAnnotation) {
        checkedValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.length() == 0) {
            return false;
        }
        for(String checkedValue : checkedValues) {
            if(value.equals(checkedValue)) {
                return true;
            }
        }
        return false;
    }
}
