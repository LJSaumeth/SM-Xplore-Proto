package com.smxplore.proto.domain.exceptions.activity;

import java.util.UUID;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(UUID activityId) {
        super("Activity with id '" + activityId + "' was not found.");
    }
}
