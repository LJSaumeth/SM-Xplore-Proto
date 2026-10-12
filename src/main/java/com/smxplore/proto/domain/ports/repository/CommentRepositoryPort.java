package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.securityreport.Comment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CommentRepositoryPort {
    Mono<Comment> save(Comment comment);
    Flux<Comment> findAllByReportId(UUID reportId);

}
