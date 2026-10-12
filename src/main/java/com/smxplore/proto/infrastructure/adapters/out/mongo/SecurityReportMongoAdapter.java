package com.smxplore.proto.infrastructure.adapters.out.mongo;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SecurityReportDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.mapper.SecurityReportPersistenceMapper;
import com.smxplore.proto.infrastructure.adapters.out.mongo.repository.SecurityReportMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SecurityReportMongoAdapter implements SecurityReportRepositoryPort {

    private static final String ID_FIELD = "_id";
    private static final String TEXT_FIELD = "text";
    private static final String STATUS_FIELD = "status";

    private final SecurityReportMongoRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final SecurityReportPersistenceMapper mapper;

    @Override
    public Mono<SecurityReport> save(SecurityReport securityReport) {
        return repository.save(mapper.toDocument(securityReport))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<SecurityReport> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<SecurityReport> findAllByStatus(SecurityReportStatus status) {
        return repository.findAllByStatus(status).map(mapper::toDomain);
    }

    @Override
    public Flux<SecurityReport> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<Void> changeText(UUID reportId, String content) {
        return updateFirst(reportId, new Update().set(TEXT_FIELD, content)).then();
    }

    @Override
    public Mono<Void> changeStatus(UUID reportId, SecurityReportStatus status) {
        return updateFirst(reportId, new Update().set(STATUS_FIELD, status)).then();
    }

    @Override
    public Flux<SecurityReport> findAllByUserId(UUID userId) {
        return repository.findAllByUser_Id(userId).map(mapper::toDomain);
    }

    private Mono<?> updateFirst(UUID reportId, Update update) {
        var query = Query.query(Criteria.where(ID_FIELD).is(reportId));
        return mongoTemplate.updateFirst(query, update, SecurityReportDocument.class);
    }
}
