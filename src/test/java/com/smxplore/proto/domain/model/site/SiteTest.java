package com.smxplore.proto.domain.model.site;

import com.smxplore.proto.domain.exceptions.InvalidSiteException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteTest {

    private static final String URL = "https://example.com/photo.jpg";

    private Site validSite() {
        return new Site(UUID.randomUUID(), UUID.randomUUID(), "Tayrona", 11.3, -74.0, List.of(URL));
    }

    private Site siteWith(UUID id, UUID zoneId, String name, Double lat, Double lon, List<String> urls) {
        return new Site(id, zoneId, name, lat, lon, urls);
    }

    @Test
    void createsValidSite() {
        Site site = validSite();
        assertEquals("Tayrona", site.getName());
        assertEquals(List.of(URL), site.getPhotoUrls());
    }

    @Test
    void acceptsBoundaryCoordinates() {
        siteWith(UUID.randomUUID(), UUID.randomUUID(), "A", 90.0, 180.0, List.of());
        siteWith(UUID.randomUUID(), UUID.randomUUID(), "A", -90.0, -180.0, List.of());
    }

    @Test
    void rejectsNullIds() {
        assertThrows(InvalidSiteException.class,
                () -> siteWith(null, UUID.randomUUID(), "A", 0.0, 0.0, List.of()));
        assertThrows(InvalidSiteException.class,
                () -> siteWith(UUID.randomUUID(), null, "A", 0.0, 0.0, List.of()));
    }

    @Test
    void rejectsInvalidNames() {
        UUID id = UUID.randomUUID();
        UUID zone = UUID.randomUUID();
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, null, 0.0, 0.0, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "", 0.0, 0.0, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "  ", 0.0, 0.0, List.of()));
    }

    @Test
    void rejectsInvalidCoordinates() {
        UUID id = UUID.randomUUID();
        UUID zone = UUID.randomUUID();
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 90.1, 0.0, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", -90.1, 0.0, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, 180.1, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, -180.1, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", null, 0.0, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, null, List.of()));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", Double.NaN, 0.0, List.of()));
    }

    @Test
    void rejectsInvalidPhotoUrls() {
        UUID id = UUID.randomUUID();
        UUID zone = UUID.randomUUID();
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, 0.0, null));
        assertThrows(InvalidSiteException.class,
                () -> siteWith(id, zone, "A", 0.0, 0.0, Arrays.asList(URL, null)));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, 0.0, List.of(" ")));
        assertThrows(InvalidSiteException.class, () -> siteWith(id, zone, "A", 0.0, 0.0, List.of("not a url")));
        assertThrows(InvalidSiteException.class,
                () -> siteWith(id, zone, "A", 0.0, 0.0, List.of("ftp://example.com/a.jpg")));
    }

    @Test
    void photoUrlsAreProtectedFromExternalChanges() {
        List<String> source = new ArrayList<>(List.of(URL));
        Site site = new Site(UUID.randomUUID(), UUID.randomUUID(), "A", 0.0, 0.0, source);
        source.add("https://example.com/other.jpg");
        assertEquals(1, site.getPhotoUrls().size());
        assertThrows(UnsupportedOperationException.class, () -> site.getPhotoUrls().add("x"));
    }

    @Test
    void updateMethodsApplyValidation() {
        Site site = validSite();
        UUID newZone = UUID.randomUUID();

        site.rename("New name");
        site.relocate(1.0, 2.0);
        site.changeZone(newZone);
        site.addPhotoUrl("https://example.com/b.jpg");

        assertEquals("New name", site.getName());
        assertEquals(1.0, site.getLatitude());
        assertEquals(2.0, site.getLongitude());
        assertEquals(newZone, site.getZoneId());
        assertEquals(2, site.getPhotoUrls().size());

        site.removePhotoUrl(URL);
        assertTrue(!site.getPhotoUrls().contains(URL));

        assertThrows(InvalidSiteException.class, () -> site.rename(" "));
        assertThrows(InvalidSiteException.class, () -> site.relocate(100.0, 0.0));
        assertThrows(InvalidSiteException.class, () -> site.changeZone(null));
        assertThrows(InvalidSiteException.class, () -> site.addPhotoUrl("bad"));
        assertThrows(InvalidSiteException.class, () -> site.replacePhotoUrls(List.of("bad")));
        assertEquals(1.0, site.getLatitude());
    }
}
