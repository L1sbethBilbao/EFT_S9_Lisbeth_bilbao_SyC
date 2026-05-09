package com.duoc.backendS8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.duoc.backendS8.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BackendS8Application {

	public static void main(String[] args) {
		SpringApplication.run(BackendS8Application.class, args);
	}

}
