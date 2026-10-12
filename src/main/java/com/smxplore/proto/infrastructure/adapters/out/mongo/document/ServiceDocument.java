package com.smxplore.proto.infrastructure.adapters.out.mongo.document;

import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "services")
@CompoundIndexes({
        @CompoundIndex(name = "status_type_idx", def = "{'status': 1, 'type': 1}"),
        @CompoundIndex(name = "provider_created_idx", def = "{'provider.id': 1, 'createdAt': -1}")
})
public record ServiceDocument(
        @Id
        UUID id,
        String name,
        ServiceType type,
        ProviderRefDocument provider,
        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        GeoJsonPoint location,
        BigDecimal price,
        List<String> photosUrls,
        ServiceStatus status,
        Instant createdAt,
        List<AttributeDocument> extras
) {
}
