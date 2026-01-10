package com.titta.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "app.cors.allowed-origins=*")
@ActiveProfiles("test")
class TittaApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
