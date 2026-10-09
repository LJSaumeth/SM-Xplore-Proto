package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

import java.util.UUID;

public interface ConsultReportCommentsUseCasePort {
    Page<Comment> handle(UUID reportId, PageRequest pageRequest);
}
