package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.Comment;

import java.util.Optional;
import java.util.UUID;

public interface CommentReportUseCasePort {
    Optional<Long> handle(UUID reportId, Comment comment);
}
