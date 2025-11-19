package io.huyvo.securecapita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
/*
	exclude = { SecurityAutoConfiguration.class }:
	- Tắt tính năng Spring Security tự cấu hình (auto-configuration) của Spring Boot.
	- Nếu bạn không exclude, Spring Security mặc định sẽ:
	 + chặn mọi request
	 + yêu cầu login form
     + tạo mật khẩu random mỗi lần chạy app
 */
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
