package com.smxplore.proto.domain.model.zone;

import com.smxplore.proto.domain.exceptions.InvalidZoneException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZoneTest {

    @Test
    void createsValidZoneAndStripsName() {
        UUID id = UUID.randomUUID();
        Zone zone = new Zone(id, "  Rodadero ");
        assertEquals(id, zone.getId());
        assertEquals("Rodadero", zone.getName());
    }

    @Test
    void rejectsNullId() {
        assertThrows(InvalidZoneException.class, () -> new Zone(null, "North"));
    }

    @Test
    void rejectsInvalidNames() {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidZoneException.class, () -> new Zone(id, null));
        assertThrows(InvalidZoneException.class, () -> new Zone(id, ""));
        assertThrows(InvalidZoneException.class, () -> new Zone(id, "   "));
    }

    @Test
    void renameValidatesName() {
        Zone zone = new Zone(UUID.randomUUID(), "North");
        zone.rename("South");
        assertEquals("South", zone.getName());
        assertThrows(InvalidZoneException.class, () -> zone.rename(" "));
        assertEquals("South", zone.getName());
    }
}
