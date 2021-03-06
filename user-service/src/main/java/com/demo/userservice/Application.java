package com.demo.userservice;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder();
	}

	@Bean
	Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()).build());
	}

}
