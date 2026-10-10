package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {

    Mono<User> save(User user);

    Mono<User> findById(UUID id);

    Mono<User> findByEmail(String email);

    Mono<User> findByPhone(String phone);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPhone(String phone);

    Flux<User> findAll();

    Flux<User> search(UserSearchCriteria criteria);

    Mono<Void> deleteById(UUID id);
}
