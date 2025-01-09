package com.api.HbSolution;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.api.HbSolution.config.SecurityConfig;

@SpringBootTest
@Import(SecurityConfig.class)
class HbSolutionApplicationTests {

	@Test
	void contextLoads() {
	}

}
