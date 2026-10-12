package com.smxplore.proto.infrastructure.adapters.out.mongo.repository;

import com.smxplore.proto.infrastructure.adapters.out.mongo.document.CommentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CommentMongoRepository extends ReactiveMongoRepository<CommentDocument, UUID> {

    Flux<CommentDocument> findAllByReportId(UUID reportId);
}
