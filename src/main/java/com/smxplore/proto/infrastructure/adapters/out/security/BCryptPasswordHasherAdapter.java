package com.smxplore.proto.infrastructure.adapters.out.security;

import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Mono<String> hash(String rawPassword) {
        return Mono.fromCallable(() -> encoder.encode(rawPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> matches(String rawPassword, String passwordHash) {
        return Mono.fromCallable(() -> passwordHash != null
                        && rawPassword != null
                        && encoder.matches(rawPassword, passwordHash))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
