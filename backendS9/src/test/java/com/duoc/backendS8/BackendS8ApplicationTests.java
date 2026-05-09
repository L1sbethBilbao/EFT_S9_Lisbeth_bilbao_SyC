package com.duoc.backendS8;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod;

// Invoca main() al levantar el contexto (por defecto @SpringBootTest no lo ejecuta → JaCoCo 0% en main).
@SpringBootTest(useMainMethod = UseMainMethod.ALWAYS)
class BackendS8ApplicationTests {

	@Test
	void contextLoads() {
	}

}
