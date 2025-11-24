package com.fitness.apigateway;


import com.fitness.apigateway.user.RegisterRequest;
import com.fitness.apigateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements GlobalFilter, Ordered {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegisterRequest registerRequest = getUserDetails(token);

        if (registerRequest == null || registerRequest.getKeycloakId() == null) {
            return chain.filter(exchange);
        }

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");

        if (userId == null) {
            userId = registerRequest.getKeycloakId();
        }

        String finalUserId = userId;

        return userService.validateUser(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return userService.registerUser(registerRequest).then();
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(r -> r.headers(h -> h.set("X-User-ID", finalUserId)))
                            .build();

                    return chain.filter(mutatedExchange);
                }));
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claimsSet.getStringClaim("email"));
            registerRequest.setKeycloakId(claimsSet.getStringClaim("sub"));
            registerRequest.setFirstName(claimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(claimsSet.getStringClaim("family_name"));
            registerRequest.setPassword("dummy@1234123");

            return registerRequest;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

}