package com.cinebyte.cinebyte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CineByteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CineByteApplication.class, args);
	}

}
