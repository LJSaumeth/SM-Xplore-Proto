package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.Comment;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsultReportCommentsUseCasePort {
    Flux<Comment> handle(UUID reportId);
}
