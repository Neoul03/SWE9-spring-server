package com.example.comitserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SWE9ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SWE9ServerApplication.class, args);
	}
}