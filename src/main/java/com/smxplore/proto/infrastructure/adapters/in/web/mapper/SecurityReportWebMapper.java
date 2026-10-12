package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.CommentRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.CommentResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.LocationDto;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SecurityReportRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SecurityReportResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UserRefDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityReportWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SecurityReport toDomain(SecurityReportRequest request);

    SecurityReportResponse toResponse(SecurityReport securityReport);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportId", ignore = true)
    Comment toDomain(CommentRequest request);

    CommentResponse toResponse(Comment comment);

    UserRef toDomain(UserRefDto userRef);

    UserRefDto toDto(UserRef userRef);

    Location toDomain(LocationDto location);

    LocationDto toDto(Location location);
}
