package com.servicepoint.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServicePointApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePointApplication.class, args);
	}

}
