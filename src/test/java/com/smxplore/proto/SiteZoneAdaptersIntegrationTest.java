package com.smxplore.proto;

import static org.assertj.core.api.Assertions.assertThat;

import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SiteZoneAdaptersIntegrationTest {

    @Autowired
    private IZoneRepository zoneRepository;

    @Autowired
    private ISiteRepository siteRepository;

    @Test
    void zoneRoundTripKeepsTheName() {
        var zone = new Zone(UUID.randomUUID(), "Historic Center");
        zoneRepository.save(zone).block();

        var found = zoneRepository.findById(zone.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Historic Center");
        assertThat(zoneRepository.existsById(zone.getId()).block()).isTrue();
        assertThat(zoneRepository.findAll().collectList().block()).isNotEmpty();
    }

    @Test
    void siteRoundTripKeepsCoordinatesAndSupportsZoneFiltering() {
        var zone = new Zone(UUID.randomUUID(), "Tayrona");
        zoneRepository.save(zone).block();

        var site = new Site(UUID.randomUUID(), zone.getId(), "Playa Cristal", 11.31, -74.06,
                List.of("https://example.com/cristal.jpg"));
        siteRepository.save(site).block();

        var found = siteRepository.findById(site.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getZoneId()).isEqualTo(zone.getId());
        assertThat(found.getPhotoUrls()).containsExactly("https://example.com/cristal.jpg");

        var byZone = siteRepository.findAllByZoneId(zone.getId()).collectList().block();
        assertThat(byZone).isNotNull().isNotEmpty();
    }
}
