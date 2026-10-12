package com.smxplore.proto.infrastructure.adapters.out.mongo;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.ports.repository.CommentRepositoryPort;
import com.smxplore.proto.infrastructure.adapters.out.mongo.mapper.CommentPersistenceMapper;
import com.smxplore.proto.infrastructure.adapters.out.mongo.repository.CommentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentMongoAdapter implements CommentRepositoryPort {

    private final CommentMongoRepository repository;
    private final CommentPersistenceMapper mapper;

    @Override
    public Mono<Comment> save(Comment comment) {
        return repository.save(mapper.toDocument(comment)).map(mapper::toDomain);
    }

    @Override
    public Flux<Comment> findAllByReportId(UUID reportId) {
        return repository.findAllByReportId(reportId).map(mapper::toDomain);
    }
}
