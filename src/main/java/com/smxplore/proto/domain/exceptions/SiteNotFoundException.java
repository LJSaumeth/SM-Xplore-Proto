package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class SiteNotFoundException extends RuntimeException {

    public SiteNotFoundException(UUID siteId) {
        super("Site '" + siteId + "' was not found.");
    }
}
