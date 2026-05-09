package com.duoc.frontendS8.validation;

import java.util.regex.Pattern;

/**
 * Detecta patrones típicos de marcado o handlers de eventos en texto libre (mitigación XSS en servidor).
 */
public final class InputSafety {

	private static final Pattern TAG_OPEN = Pattern.compile("<\\s*[a-zA-Z!/]");
	private static final Pattern SCRIPTISH = Pattern.compile("(?i)javascript:|</?script|on[a-z]+\\s*=");

	private InputSafety() {
	}

	public static boolean containsDangerousMarkup(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		return TAG_OPEN.matcher(value).find() || SCRIPTISH.matcher(value).find();
	}
}
