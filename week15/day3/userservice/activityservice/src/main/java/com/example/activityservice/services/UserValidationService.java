package com.example.activityservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j // Add Logging
public class UserValidationService {

    @Qualifier("userServiceWebClient")
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info("Attempting to call USER-SERVICE for user: {}", userId);

        try {
            // CHECK: Does USERSERVICE actually have a controller at /api/users/{id}/validate?
            Boolean response = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // Blocking is okay for testing, but consider async later

            log.info("User validation response: {}", response);
            return Boolean.TRUE.equals(response);

        } catch (WebClientResponseException e) {
            // This handles 4xx and 5xx errors specifically
            log.error("Error response from User Service: Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            // This handles connection issues (UnknownHost, Refused)
            log.error("Communication failure with User Service", e);
            return false;
        }
    }
}