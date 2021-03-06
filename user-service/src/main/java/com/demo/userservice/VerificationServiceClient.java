
package com.demo.userservice;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class VerificationServiceClient {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final Timer verifyNewUserTimer;

    public VerificationServiceClient(RestTemplateBuilder restTemplateBuilder, DiscoveryClient discoveryClient,
            CircuitBreakerFactory circuitBreakerFactory, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplateBuilder.build();
        this.discoveryClient = discoveryClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.verifyNewUserTimer = meterRegistry.timer("verifyNewUser");
    }

    public VerificationResult verifyNewUser(UUID userUuid, int userAge) {
        return verifyNewUserTimer.record(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("gateway");
            ServiceInstance instance = instances.stream().findAny()
                    .orElseThrow(() -> new IllegalStateException("No gateway instance available"));
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromHttpUrl(instance.getUri().toString() + "/fraud-verifier/users/verify")
                    .queryParam("uuid", userUuid).queryParam("age", userAge);
            return circuitBreakerFactory.create("verifyNewUser").run(
                    () -> restTemplate.getForObject(uriComponentsBuilder.toUriString(), VerificationResult.class),
                    throwable -> userRejected(userUuid, userAge));
        });
    }

    private VerificationResult userRejected(UUID userUuid, int userAge) {
        return VerificationResult.failed(userUuid);
    }
}