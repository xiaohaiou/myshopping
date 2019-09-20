package com.mutil.userful.util;

import com.mutil.userful.domain.requestparam.ValidateResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorUtil {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <T> ValidateResult validator(T object) {
        if (null == object) {
            return new ValidateResult(false,
                    new String[]{"The object to be validated must not be null."});
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        int errSize = violations.size();

        String[] errMsg = new String[errSize];
        boolean result = true;
        if (errSize > 0) {
            int i = 0;
            for (ConstraintViolation<T> violation : violations) {
                errMsg[i] = violation.getMessage();
                i++;
            }
            result = false;
        }

        return new ValidateResult(result, errMsg);
    }

}
