package com.duoc.frontendS8.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = SafePlainTextValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SafePlainText {

	String message() default "No se permiten etiquetas HTML ni scripts en este campo.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
