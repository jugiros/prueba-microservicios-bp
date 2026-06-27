package com.bp.msclientes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.flyway.enabled=false"
})
class MsClientesApplicationTests {

	@Test
	void contextLoads() {
	}
}
