package com.api.HbSolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.api.HbSolution.entity")
public class HbSolutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(HbSolutionApplication.class, args);

	}

}
