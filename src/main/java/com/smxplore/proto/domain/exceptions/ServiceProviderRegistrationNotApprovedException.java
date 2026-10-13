package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class ServiceProviderRegistrationNotApprovedException extends RuntimeException {

    public ServiceProviderRegistrationNotApprovedException(UUID serviceProviderId) {
        super("The registration of service provider '" + serviceProviderId + "' is not approved yet.");
    }
}
