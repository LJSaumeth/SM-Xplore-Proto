package com.smxplore.proto.infrastructure.adapters.out.mongo.mapper;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SecurityReportDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.UserRefDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Mapper(componentModel = "spring")
public interface SecurityReportPersistenceMapper {

    SecurityReportDocument toDocument(SecurityReport report);

    @Mapping(target = "photosUrls", expression = "java(document.photosUrls() == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(document.photosUrls()))")
    @Mapping(target = "status", expression = "java(document.status() == null ? SecurityReportStatus.ONGOING : document.status())")
    @Mapping(target = "createdAt", expression = "java(document.createdAt() == null ? java.time.Instant.now() : document.createdAt())")
    SecurityReport toDomain(SecurityReportDocument document);

    UserRefDocument toDocument(UserRef userRef);

    UserRef toDomain(UserRefDocument document);

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
}
