package com.smxplore.proto.domain.ports.security;

import reactor.core.publisher.Mono;

public interface PasswordHasherPort {

    Mono<String> hash(String rawPassword);

    Mono<Boolean> matches(String rawPassword, String passwordHash);
}
