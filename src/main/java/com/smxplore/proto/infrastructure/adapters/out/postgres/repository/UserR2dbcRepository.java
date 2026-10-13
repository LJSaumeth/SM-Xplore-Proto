package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserR2dbcRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    // Case-insensitive so /auth/login accepts any casing of the registered email
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email)")
    Mono<UserEntity> findByEmail(String email);

    Mono<UserEntity> findByPhone(String phone);

    @Query("SELECT COUNT(*) > 0 FROM users WHERE LOWER(email) = LOWER(:email)")
    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPhone(String phone);

    Flux<UserEntity> findAllByRole(UserRole role);

    Flux<UserEntity> findAllByStatus(UserStatus status);
}
