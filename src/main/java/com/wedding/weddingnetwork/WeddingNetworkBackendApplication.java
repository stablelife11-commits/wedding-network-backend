package com.wedding.weddingnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WeddingNetworkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeddingNetworkBackendApplication.class, args);
	}

}
