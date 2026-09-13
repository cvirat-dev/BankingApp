package com.demo.benachrichtigung_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(
								"http://localhost:4200",
								"https://icy-flower-0779f600f.5.azurestaticapps.net")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
			}
		};
	}
}