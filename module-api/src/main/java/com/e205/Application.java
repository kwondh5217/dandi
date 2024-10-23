package com.e205;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.e205")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
