package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.ports.repository.CommentRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportCommentsUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultReportCommentUseCase implements ConsultReportCommentsUseCasePort {
    private final CommentRepositoryPort commentRepo;

    @Override
    public Flux<Comment> handle(UUID reportId) {
        return commentRepo.findAllByReportId(reportId);
    }
}
