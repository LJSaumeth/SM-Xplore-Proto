package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.DestinationManagerRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DestinationManagerR2dbcAdapter implements DestinationManagerRepositoryPort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<DestinationManager> save(DestinationManager user) {
        return userRepository.save(user).map(DestinationManager.class::cast);
    }

    @Override
    public Mono<DestinationManager> findById(UUID id) {
        return userRepository.findById(id).map(DestinationManager.class::cast);
    }

    @Override
    public Mono<DestinationManager> findByEmail(String email) {
        return userRepository.findByEmail(email).map(DestinationManager.class::cast);
    }

    @Override
    public Flux<DestinationManager> findAll() {
        return userRepository.search(new UserSearchCriteria(UserRole.DESTINATION_MANAGER, null, null)).map(DestinationManager.class::cast);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }
}
