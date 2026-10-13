package com.smxplore.proto.domain.ports.security;

import com.smxplore.proto.domain.model.user.User;
import reactor.core.publisher.Mono;

public interface TokenIssuerPort {

    Mono<IssuedToken> issue(User user);

    record IssuedToken(String value, long expiresInSeconds) {
    }
}
