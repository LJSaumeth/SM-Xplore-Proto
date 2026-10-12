package com.smxplore.proto.infrastructure.adapters.out.mongo.document;

import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "security_reports")
@CompoundIndexes({
        @CompoundIndex(name = "status_created_idx", def = "{'status': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "user_created_idx", def = "{'user.id': 1, 'createdAt': -1}")
})
public record SecurityReportDocument(
        @Id
        UUID id,
        UserRefDocument user,
        String text,
        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        GeoJsonPoint location,
        List<String> photosUrls,
        SecurityReportStatus status,
        Instant createdAt
) {
}
