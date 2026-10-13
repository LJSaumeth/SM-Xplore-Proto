package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.ports.repository.AdminRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminR2dbcAdapter implements AdminRepositoryPort {

    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Admin> save(Admin user) {
        return userRepository.save(user).map(Admin.class::cast);
    }

    @Override
    public Mono<Admin> findById(UUID id) {
        return userRepository.findById(id).map(Admin.class::cast);
    }

    @Override
    public Mono<Admin> findByEmail(String email) {
        return userRepository.findByEmail(email).map(Admin.class::cast);
    }

    @Override
    public Flux<Admin> findAll() {
        return userRepository.search(new UserSearchCriteria(UserRole.ADMIN, null, null)).map(Admin.class::cast);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }
}
