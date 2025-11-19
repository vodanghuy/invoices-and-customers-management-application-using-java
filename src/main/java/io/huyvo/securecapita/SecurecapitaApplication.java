package io.huyvo.securecapita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class SecurecapitaApplication {

	private static final int STRENGTH = 12;

	public static void main(String[] args) {
		SpringApplication.run(SecurecapitaApplication.class, args);
	}

	@Bean
	public static BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(STRENGTH);
	}
}
