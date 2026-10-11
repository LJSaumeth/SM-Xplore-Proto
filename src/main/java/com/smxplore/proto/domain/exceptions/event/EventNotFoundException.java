package com.smxplore.proto.domain.exceptions.event;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventNotFoundException(UUID eventId) {
        super("Event with id '" + eventId + "' was not found.");
    }
}
