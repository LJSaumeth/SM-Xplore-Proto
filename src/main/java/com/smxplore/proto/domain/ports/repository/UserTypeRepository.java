package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserTypeRepository<T extends User> {

    Mono<T> save(T user);

    Mono<T> findById(UUID id);

    Mono<T> findByEmail(String email);

    Flux<T> findAll();

    Mono<Void> deleteById(UUID id);
}
