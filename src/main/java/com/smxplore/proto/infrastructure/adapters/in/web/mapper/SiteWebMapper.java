package com.smxplore.proto.infrastructure.adapters.in.web.mapper;

import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SiteRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SiteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SiteWebMapper {

    public SiteResponse toResponse(Site site) {
        return new SiteResponse(site.getId(), site.getZoneId(), site.getName(), site.getLatitude(),
                site.getLongitude(), site.getPhotoUrls());
    }

    public Site toDomain(SiteRequest request) {
        return new Site(UUID.randomUUID(), request.zoneId(), request.name(), request.latitude(),
                request.longitude(), request.photoUrls() == null ? List.of() : request.photoUrls());
    }
}
