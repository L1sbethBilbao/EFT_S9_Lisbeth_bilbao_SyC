package com.duoc.frontendS8.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InputSafetyTest {

	@Test
	void permiteTextoPlanoYNumerosEnNombre() {
		assertFalse(InputSafety.containsDangerousMarkup(null));
		assertFalse(InputSafety.containsDangerousMarkup(""));
		assertFalse(InputSafety.containsDangerousMarkup("Perro mestizo"));
		assertFalse(InputSafety.containsDangerousMarkup("<3 los animales"));
	}

	@Test
	void detectaEtiquetasYScripts() {
		assertTrue(InputSafety.containsDangerousMarkup("<script>alert(1)</script>"));
		assertTrue(InputSafety.containsDangerousMarkup("<img src=x onerror=alert(1)>"));
		assertTrue(InputSafety.containsDangerousMarkup("javascript:void(0)"));
		assertTrue(InputSafety.containsDangerousMarkup("</script>"));
	}
}
