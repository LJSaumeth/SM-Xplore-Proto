package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.GuideRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GuideR2dbcAdapter implements GuideRepositoryPort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Guide> save(Guide user) {
        return userRepository.save(user).map(Guide.class::cast);
    }

    @Override
    public Mono<Guide> findById(UUID id) {
        return userRepository.findById(id).ofType(Guide.class);
    }

    @Override
    public Mono<Guide> findByEmail(String email) {
        return userRepository.findByEmail(email).ofType(Guide.class);
    }

    @Override
    public Flux<Guide> findAll() {
        return userRepository.search(new UserSearchCriteria(UserRole.GUIDE, null, null)).map(Guide.class::cast);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }
}
