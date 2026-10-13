package com.smxplore.proto.domain.model.site;

import com.smxplore.proto.domain.exceptions.InvalidSiteException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A tourist site located in a zone. The zone is referenced only by its
 * identifier; whether it actually exists must be verified by the application layer.
 */
public class Site {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    private final UUID id;
    private UUID zoneId;
    private String name;
    private Double latitude;
    private Double longitude;
    private final List<String> photoUrls;

    public Site(UUID id, UUID zoneId, String name, Double latitude, Double longitude,
                Collection<String> photoUrls) {
        if (id == null) {
            throw new InvalidSiteException("Site id must not be null");
        }
        this.id = id;
        this.zoneId = validateZoneId(zoneId);
        this.name = validateName(name);
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrls = new ArrayList<>(validatePhotoUrls(photoUrls));
    }

    public UUID getId() {
        return id;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /** Returns an unmodifiable view; use the domain methods to change photos. */
    public List<String> getPhotoUrls() {
        return List.copyOf(photoUrls);
    }

    public void rename(String newName) {
        this.name = validateName(newName);
    }

    public void relocate(Double newLatitude, Double newLongitude) {
        validateCoordinates(newLatitude, newLongitude);
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }

    public void changeZone(UUID newZoneId) {
        this.zoneId = validateZoneId(newZoneId);
    }

    public void replacePhotoUrls(Collection<String> newPhotoUrls) {
        List<String> validated = validatePhotoUrls(newPhotoUrls);
        this.photoUrls.clear();
        this.photoUrls.addAll(validated);
    }

    public void addPhotoUrl(String photoUrl) {
        this.photoUrls.add(validatePhotoUrl(photoUrl));
    }

    public void removePhotoUrl(String photoUrl) {
        this.photoUrls.remove(photoUrl);
    }

    private static UUID validateZoneId(UUID zoneId) {
        if (zoneId == null) {
            throw new InvalidSiteException("Site zone id must not be null");
        }
        return zoneId;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidSiteException("Site name must not be null, empty or blank");
        }
        return name.strip();
    }

    private static void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new InvalidSiteException("Site latitude and longitude must not be null");
        }
        if (latitude.isNaN() || latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new InvalidSiteException("Site latitude must be between -90 and 90");
        }
        if (longitude.isNaN() || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new InvalidSiteException("Site longitude must be between -180 and 180");
        }
    }

    private static List<String> validatePhotoUrls(Collection<String> photoUrls) {
        if (photoUrls == null) {
            throw new InvalidSiteException("Site photo URLs must not be null");
        }
        List<String> validated = new ArrayList<>(photoUrls.size());
        for (String url : photoUrls) {
            validated.add(validatePhotoUrl(url));
        }
        return validated;
    }

    private static String validatePhotoUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidSiteException("Site photo URL must not be null, empty or blank");
        }
        String trimmed = url.strip();
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            boolean httpScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            if (!httpScheme || uri.getHost() == null) {
                throw new InvalidSiteException("Site photo URL must be a valid http or https URL: " + url);
            }
        } catch (URISyntaxException e) {
            throw new InvalidSiteException("Site photo URL is malformed: " + url);
        }
        return trimmed;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Site other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
