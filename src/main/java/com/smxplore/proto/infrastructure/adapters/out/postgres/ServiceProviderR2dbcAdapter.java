package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.ServiceProviderRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServiceProviderR2dbcAdapter implements ServiceProviderRepositoryPort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<ServiceProvider> save(ServiceProvider user) {
        return userRepository.save(user).map(ServiceProvider.class::cast);
    }

    @Override
    public Mono<ServiceProvider> findById(UUID id) {
        return userRepository.findById(id).map(ServiceProvider.class::cast);
    }

    @Override
    public Mono<ServiceProvider> findByEmail(String email) {
        return userRepository.findByEmail(email).map(ServiceProvider.class::cast);
    }

    @Override
    public Flux<ServiceProvider> findAll() {
        return userRepository.search(new UserSearchCriteria(UserRole.SERVICE_PROVIDER, null, null)).map(ServiceProvider.class::cast);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }
}
