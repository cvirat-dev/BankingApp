package com.demo.benachrichtigung_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "demo.auth.password=test")
@ActiveProfiles("dev")
class BenachrichtigungServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
