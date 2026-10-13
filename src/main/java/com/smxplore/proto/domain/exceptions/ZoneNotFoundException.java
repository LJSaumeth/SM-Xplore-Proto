package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class ZoneNotFoundException extends RuntimeException {

    public ZoneNotFoundException(UUID zoneId) {
        super("Zone '" + zoneId + "' was not found.");
    }
}
