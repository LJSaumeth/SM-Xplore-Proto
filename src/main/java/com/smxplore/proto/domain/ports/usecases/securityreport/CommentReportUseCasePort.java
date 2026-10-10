package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.Comment;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface CommentReportUseCasePort {
    Mono<Comment> handle(Comment comment);
}
