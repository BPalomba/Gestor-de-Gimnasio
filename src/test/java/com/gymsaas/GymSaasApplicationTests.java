package com.gymsaas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GymSaasApplicationTests {

	@Test
	void contextLoads() {
		// Verifica que el contexto de Spring levanta correctamente
		// usando H2 en memoria (profile test)
	}
}