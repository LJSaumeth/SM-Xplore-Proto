package com.smxplore.proto.infrastructure.adapters.out.mongo;

import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SiteDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.repository.SiteMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SiteMongoAdapter implements ISiteRepository {

    private final SiteMongoRepository repository;

    @Override
    public Mono<Site> save(Site site) {
        return repository.save(toDocument(site)).map(SiteMongoAdapter::toDomain);
    }

    @Override
    public Mono<Site> findById(UUID id) {
        return repository.findById(id).map(SiteMongoAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Flux<Site> findAll() {
        return repository.findAll().map(SiteMongoAdapter::toDomain);
    }

    @Override
    public Flux<Site> findAllByZoneId(UUID zoneId) {
        return repository.findAllByZoneId(zoneId).map(SiteMongoAdapter::toDomain);
    }

    private static SiteDocument toDocument(Site site) {
        return new SiteDocument(site.getId(), site.getZoneId(), site.getName(), site.getLatitude(),
                site.getLongitude(), site.getPhotoUrls());
    }

    private static Site toDomain(SiteDocument document) {
        var photoUrls = document.photoUrls() == null ? List.<String>of() : document.photoUrls();
        return new Site(document.id(), document.zoneId(), document.name(), document.latitude(),
                document.longitude(), photoUrls);
    }
}
