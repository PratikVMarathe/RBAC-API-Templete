package com.academy.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PostalCodeValidator.class)
@Target({ ElementType.TYPE }) // Apply to class level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPostalCode {
    String message() default "Invalid postal code format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
