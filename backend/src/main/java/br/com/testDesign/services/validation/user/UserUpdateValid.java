package br.com.testDesign.services.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default template for custom Constraint Validator.
 */
@Constraint(validatedBy = UserUpdateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserUpdateValid {

    String message() default "Validation error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}