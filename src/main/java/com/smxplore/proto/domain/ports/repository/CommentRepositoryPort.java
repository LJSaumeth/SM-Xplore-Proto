package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepositoryPort {
    Optional<Long> save(Comment comment);
    Page<Comment> findAllByReportId(UUID reportId, PageRequest pageRequest);

}
