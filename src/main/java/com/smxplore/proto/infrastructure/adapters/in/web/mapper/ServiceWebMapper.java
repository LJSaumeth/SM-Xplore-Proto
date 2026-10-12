package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.model.types.AttributeMap;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AttributeDto;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.LocationDto;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ProviderRefDto;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ServiceWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "extras", expression = "java(toAttributeMap(request.extras()))")
    Service toDomain(ServiceRequest request);

    ServiceResponse toResponse(Service service);

    ProviderRef toDomain(ProviderRefDto providerRef);

    ProviderRefDto toDto(ProviderRef providerRef);

    Location toDomain(LocationDto location);

    LocationDto toDto(Location location);

    /**
     * AttributeMap is a domain type MapStruct cannot build on its own, so the
     * mapping is done manually keeping the order of the attributes.
     */
    default AttributeMap toAttributeMap(List<AttributeDto> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return AttributeMap.empty();
        }
        return AttributeMap.of(attributes.stream().map(this::toAttribute).toList());
    }

    default List<Attribute> toAttributeList(List<AttributeDto> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return List.of();
        }
        return attributes.stream().map(this::toAttribute).toList();
    }

    default List<AttributeDto> toAttributeDtos(AttributeMap attributes) {
        if (attributes == null) {
            return List.of();
        }
        return attributes.all().stream().map(this::toAttributeDto).toList();
    }

    default Attribute toAttribute(AttributeDto attributeDto) {
        return Attribute.builder()
                .name(attributeDto.name())
                .value(toDomainValue(attributeDto.value()))
                .build();
    }

    default AttributeDto toAttributeDto(Attribute attribute) {
        return new AttributeDto(attribute.name(), toDtoValue(attribute.value()));
    }

    private Object toDomainValue(Object value) {
        // Jackson deserializes nested attributes as raw maps
        if (value instanceof Map<?, ?> map && map.containsKey("name") && map.containsKey("value")) {
            return toAttribute(new AttributeDto(String.valueOf(map.get("name")), map.get("value")));
        }
        return value;
    }

    private Object toDtoValue(Object value) {
        if (value instanceof Attribute nested) {
            return toAttributeDto(nested);
        }
        return value;
    }
}
