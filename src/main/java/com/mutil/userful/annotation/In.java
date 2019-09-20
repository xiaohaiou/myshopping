package com.mutil.userful.annotation;

import com.mutil.userful.common.InValidator;

import javax.validation.*;
import java.lang.annotation.*;

@Documented
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InValidator.class})
public @interface In {

    String message() default "值不在给定范围内";

    String[] values() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
