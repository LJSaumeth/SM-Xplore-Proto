package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.exceptions.securityreport.InvalidCommentException;
import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.ports.repository.CommentRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.CommentReportUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CommentReportUseCase implements CommentReportUseCasePort {
    private final CommentRepositoryPort commentRepo;
    private static long serialVersionUID = 1L;

    @Override
    public Mono<Comment> handle(Comment comment) {
        return Mono.justOrEmpty(comment)
                .switchIfEmpty(Mono.error(new InvalidCommentException("Comment cannot be null")))
                .map(c -> c.toBuilder().id(serialVersionUID++).build())
                .doOnNext(Comment::validate)
                .flatMap(commentRepo::save);
    }
}
