package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class GuideRegistrationNotPendingException extends RuntimeException {

    public GuideRegistrationNotPendingException(UUID guideId) {
        super("The registration of guide '" + guideId + "' is not pending approval.");
    }
}
