package com.smxplore.proto.infrastructure.adapters.out.mongo.mapper;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.model.types.AttributeMap;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.AttributeDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ProviderRefDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ServiceDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ServicePersistenceMapper {

    @Mapping(target = "extras", expression = "java(toAttributeDocuments(service.getExtras()))")
    ServiceDocument toDocument(Service service);

    @Mapping(target = "photosUrls", expression = "java(document.photosUrls() == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(document.photosUrls()))")
    @Mapping(target = "status", expression = "java(document.status() == null ? ServiceStatus.AVAILABLE : document.status())")
    @Mapping(target = "createdAt", expression = "java(document.createdAt() == null ? java.time.Instant.now() : document.createdAt())")
    @Mapping(target = "extras", expression = "java(toAttributeMap(document.extras()))")
    Service toDomain(ServiceDocument document);

    ProviderRefDocument toDocument(ProviderRef providerRef);

    ProviderRef toDomain(ProviderRefDocument document);

    /**
     * Location is stored as a GeoJSON point (longitude, latitude) so a
     * 2dsphere index can be used for geospatial queries.
     */
    default GeoJsonPoint toDocument(Location location) {
        return location == null ? null : new GeoJsonPoint(location.longitude(), location.latitude());
    }

    default Location toDomain(GeoJsonPoint point) {
        return point == null ? null : new Location(point.getY(), point.getX());
    }

    /**
     * AttributeMap is a domain type MapStruct cannot build on its own, so the
     * mapping is done manually while keeping the insertion order of the map.
     */
    default List<AttributeDocument> toAttributeDocuments(AttributeMap extras) {
        if (extras == null || extras.isEmpty()) {
            return new ArrayList<>();
        }
        return extras.all().stream()
                .map(this::toAttributeDocument)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default AttributeMap toAttributeMap(List<AttributeDocument> extras) {
        if (extras == null || extras.isEmpty()) {
            return AttributeMap.empty();
        }
        return AttributeMap.of(extras.stream().map(this::toAttribute).toList());
    }

    default AttributeDocument toAttributeDocument(Attribute attribute) {
        return new AttributeDocument(attribute.name(), toDocumentValue(attribute.value()));
    }

    default Attribute toAttribute(AttributeDocument document) {
        return Attribute.builder()
                .name(document.name())
                .value(toDomainValue(document.value()))
                .build();
    }

    private Object toDocumentValue(Object value) {
        if (value instanceof Attribute nested) {
            return toAttributeDocument(nested);
        }
        // BSON has no char type, store it as a single-character string
        if (value instanceof Character character) {
            return String.valueOf(character);
        }
        return value;
    }

    private Object toDomainValue(Object value) {
        if (value instanceof AttributeDocument nested) {
            return toAttribute(nested);
        }
        // Mongo returns nested documents as raw maps when the field is typed Object
        if (value instanceof Map<?, ?> map && map.containsKey("name") && map.containsKey("value")) {
            return Attribute.builder()
                    .name(String.valueOf(map.get("name")))
                    .value(toDomainValue(map.get("value")))
                    .build();
        }
        return value;
    }
}
