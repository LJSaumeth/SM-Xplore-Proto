package com.smxplore.proto.domain.model.zone;

import com.smxplore.proto.domain.exceptions.InvalidZoneException;

import java.util.UUID;

/**
 * A geographic or tourist zone that sites can belong to.
 * Sites reference their zone by identifier; the zone does not know its sites.
 */
public class Zone {

    private final UUID id;
    private String name;

    public Zone(UUID id, String name) {
        if (id == null) {
            throw new InvalidZoneException("Zone id must not be null");
        }
        this.id = id;
        this.name = validateName(name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        this.name = validateName(newName);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidZoneException("Zone name must not be null, empty or blank");
        }
        return name.strip();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Zone other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
