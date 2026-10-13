package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.AdminEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.DestinationManagerEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.GuideEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ServiceProviderEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.TouristEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.UserEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.mapper.UserPersistenceMapper;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.AdminR2dbcRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.DestinationManagerR2dbcRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.GuideR2dbcRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.ServiceProviderR2dbcRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.TouristR2dbcRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.UserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserR2dbcAdapter implements UserRepositoryPort {

    private final UserR2dbcRepository userRepository;
    private final AdminR2dbcRepository adminRepository;
    private final TouristR2dbcRepository touristRepository;
    private final GuideR2dbcRepository guideRepository;
    private final DestinationManagerR2dbcRepository destinationManagerRepository;
    private final ServiceProviderR2dbcRepository serviceProviderRepository;
    private final R2dbcEntityTemplate template;
    private final UserPersistenceMapper mapper;

    @Override
    @Transactional
    public Mono<User> save(User user) {
        var userEntity = mapper.toUserEntity(user);
        return upsert(userRepository, UserEntity.class, userEntity, userEntity.id())
                .then(saveSubtype(user))
                .thenReturn(user);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return userRepository.findById(id).flatMap(this::assemble);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email).flatMap(this::assemble);
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone).flatMap(this::assemble);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll().flatMap(this::assemble);
    }

    @Override
    public Flux<User> search(UserSearchCriteria criteria) {
        var sql = new StringBuilder(
                "SELECT id, full_name, email, phone, password_hash, role, status, signed_up_at FROM users WHERE 1 = 1");
        var params = new LinkedHashMap<String, Object>();
        if (criteria.role() != null) {
            sql.append(" AND role = :role");
            params.put("role", criteria.role().name());
        }
        if (criteria.status() != null) {
            sql.append(" AND status = :status");
            params.put("status", criteria.status().name());
        }
        if (criteria.term() != null && !criteria.term().isBlank()) {
            sql.append(" AND (full_name ILIKE :term OR email ILIKE :term OR phone ILIKE :term)");
            params.put("term", "%" + criteria.term().trim() + "%");
        }
        var spec = template.getDatabaseClient().sql(sql.toString());
        for (var entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }
        return spec.map((row, metadata) -> new UserEntity(
                        row.get("id", UUID.class),
                        row.get("full_name", String.class),
                        row.get("email", String.class),
                        row.get("phone", String.class),
                        row.get("password_hash", String.class),
                        UserRole.valueOf(row.get("role", String.class)),
                        UserStatus.valueOf(row.get("status", String.class)),
                        row.get("signed_up_at", OffsetDateTime.class).toInstant()))
                .all()
                .flatMap(this::assemble);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }

    private Mono<Void> saveSubtype(User user) {
        return switch (user) {
            case Admin admin -> {
                var entity = mapper.toAdminEntity(admin);
                yield upsert(adminRepository, AdminEntity.class, entity, entity.id());
            }
            case Tourist tourist -> {
                var entity = mapper.toTouristEntity(tourist);
                yield upsert(touristRepository, TouristEntity.class, entity, entity.id());
            }
            case Guide guide -> {
                var entity = mapper.toGuideEntity(guide);
                yield upsert(guideRepository, GuideEntity.class, entity, entity.id());
            }
            case DestinationManager manager -> {
                var entity = mapper.toDestinationManagerEntity(manager);
                yield upsert(destinationManagerRepository, DestinationManagerEntity.class, entity, entity.id());
            }
            case ServiceProvider provider -> {
                var entity = mapper.toServiceProviderEntity(provider);
                yield upsert(serviceProviderRepository, ServiceProviderEntity.class, entity, entity.id());
            }
            default -> Mono.error(new InvalidUserDataException("Unknown user subtype: " + user.getClass().getName()));
        };
    }

    private <T> Mono<Void> upsert(ReactiveCrudRepository<T, UUID> repository, Class<T> type, T entity, UUID id) {
        return repository.existsById(id)
                .flatMap(exists -> exists
                        ? repository.save(entity)
                        : template.insert(type).using(entity))
                .then();
    }

    private Mono<User> assemble(UserEntity user) {
        return switch (user.role()) {
            case ADMIN -> adminRepository.findById(user.id()).map(admin -> (User) mapper.toAdmin(user, admin));
            case TOURIST -> touristRepository.findById(user.id()).map(tourist -> (User) mapper.toTourist(user, tourist));
            case GUIDE -> guideRepository.findById(user.id()).map(guide -> (User) mapper.toGuide(user, guide));
            case DESTINATION_MANAGER -> destinationManagerRepository.findById(user.id()).map(manager -> (User) mapper.toDestinationManager(user, manager));
            case SERVICE_PROVIDER -> serviceProviderRepository.findById(user.id()).map(provider -> (User) mapper.toServiceProvider(user, provider));
        };
    }

}
