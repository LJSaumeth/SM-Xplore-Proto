package com.smxplore.proto.domain.exceptions.event;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("Event with id '" + eventId + "' was not found.");
    }
}
