package com.codingapi.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class BaseIntegrationTest extends CodingApiTestHelper {

	@Container
	public static final MySQLContainer<?> mysql =
			new MySQLContainer<>("mysql:8.0")
					.withDatabaseName("school")
					.withUsername("appuser")
					.withPassword("apppass");

	@DynamicPropertySource
	static void datasourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
	}


}
