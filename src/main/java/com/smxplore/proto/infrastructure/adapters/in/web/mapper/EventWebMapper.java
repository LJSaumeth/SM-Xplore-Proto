package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.event.Event;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.EventRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.EventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "expectedAttendance", ignore = true)
    Event toDomain(EventRequest request);

    EventResponse toResponse(Event event);
}
