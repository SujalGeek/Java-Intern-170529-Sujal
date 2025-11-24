package com.fitness.apigateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j // Add Logging
public class UserService {

    @Qualifier("userServiceWebClient")
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Attempting to call USER-SERVICE for user: {}", userId);

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(res -> log.info("User validation response for {}: {}", userId, res))
                .onErrorResume(WebClientResponseException.class, e -> {

                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.error("User not found: {}", userId);
                        return Mono.just(false);
                    }

                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        log.error("Invalid user request: {}", userId);
                        return Mono.just(false);
                    }

                    log.error("Unexpected error from USER-SERVICE for {}: {}", userId, e.getMessage());
                    return Mono.just(false);
                })
                .onErrorResume(e -> {
                    log.error("Communication failure with USER-SERVICE", e);
                    return Mono.just(false);
                });
    }


    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling User Registration for {}", registerRequest.getEmail());

        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {

                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
//                        log.error("Invalid user request: {}", userId);
//                        return Mono.just(false);
                        return Mono.error(new RuntimeException("Bad Request: "+e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("Unexpected error : " + e.getMessage()));
                });
    }
}