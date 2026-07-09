package com.rao.RazorPay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RazorPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RazorPayApplication.class, args);
	}

}
