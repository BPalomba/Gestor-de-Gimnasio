package com.gymsaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymSaasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymSaasApplication.class, args);
	}

}
