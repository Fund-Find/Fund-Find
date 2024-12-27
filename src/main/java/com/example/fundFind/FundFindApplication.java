package com.example.fundFind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
@EntityScan(basePackages = "com.example")
@EnableJpaAuditing
public class FundFindApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundFindApplication.class, args);
	}

}
