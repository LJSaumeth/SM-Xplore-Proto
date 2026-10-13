package com.smxplore.proto.infrastructure.adapters.out.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "sitios")
public record SiteDocument(
        @Id UUID id,
        @Indexed UUID zoneId,
        String name,
        Double latitude,
        Double longitude,
        List<String> photoUrls
) {
}
