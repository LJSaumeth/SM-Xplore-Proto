package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.TouristRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TouristR2dbcAdapter implements TouristRepositoryPort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Tourist> save(Tourist user) {
        return userRepository.save(user).map(Tourist.class::cast);
    }

    @Override
    public Mono<Tourist> findById(UUID id) {
        return userRepository.findById(id).map(Tourist.class::cast);
    }

    @Override
    public Mono<Tourist> findByEmail(String email) {
        return userRepository.findByEmail(email).map(Tourist.class::cast);
    }

    @Override
    public Flux<Tourist> findAll() {
        return userRepository.search(new UserSearchCriteria(UserRole.TOURIST, null, null)).map(Tourist.class::cast);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }
}
