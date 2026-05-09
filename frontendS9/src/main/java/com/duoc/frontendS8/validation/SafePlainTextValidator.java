package com.duoc.frontendS8.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SafePlainTextValidator implements ConstraintValidator<SafePlainText, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;
		}
		return !InputSafety.containsDangerousMarkup(value);
	}
}
