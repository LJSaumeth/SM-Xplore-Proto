package com.smxplore.proto.infrastructure.adapters.out.mongo;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ServiceDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.mapper.ServicePersistenceMapper;
import com.smxplore.proto.infrastructure.adapters.out.mongo.repository.ServiceMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServiceMongoAdapter implements ServiceRepositoryPort {

    private static final String ID_FIELD = "_id";
    private static final String STATUS_FIELD = "status";
    private static final String PRICE_FIELD = "price";

    private final ServiceMongoRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ServicePersistenceMapper mapper;

    @Override
    public Mono<Service> save(Service service) {
        return repository.save(mapper.toDocument(service))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> changeStatus(UUID serviceId, ServiceStatus newStatus) {
        return updateFirst(serviceId, new Update().set(STATUS_FIELD, newStatus));
    }

    @Override
    public Flux<Service> findAllByStatusAndType(ServiceStatus status, ServiceType type) {
        return repository.findAllByStatusAndType(status, type).map(mapper::toDomain);
    }

    @Override
    public Mono<Service> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Service> findAllByStatus(ServiceStatus status) {
        return repository.findAllByStatus(status).map(mapper::toDomain);
    }

    @Override
    public Flux<Service> findAllByProviderId(UUID providerId) {
        return repository.findAllByProvider_Id(providerId).map(mapper::toDomain);
    }

    @Override
    public Flux<Service> findAllByProviderName(String providerName) {
        return repository.findAllByProvider_Name(providerName).map(mapper::toDomain);
    }

    @Override
    public Flux<Service> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> changePrice(UUID serviceId, BigDecimal newPrice) {
        return updateFirst(serviceId, new Update().set(PRICE_FIELD, newPrice));
    }

    private Mono<Boolean> updateFirst(UUID serviceId, Update update) {
        var query = Query.query(Criteria.where(ID_FIELD).is(serviceId));
        return mongoTemplate.updateFirst(query, update, ServiceDocument.class)
                .map(result -> result.getModifiedCount() > 0);
    }
}
