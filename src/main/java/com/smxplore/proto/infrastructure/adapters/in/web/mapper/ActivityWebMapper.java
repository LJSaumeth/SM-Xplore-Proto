package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ActivityRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ActivityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Activity toDomain(ActivityRequest request);

    ActivityResponse toResponse(Activity activity);
}
