package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class ServiceProviderRegistrationNotPendingException extends RuntimeException {

    public ServiceProviderRegistrationNotPendingException(UUID serviceProviderId) {
        super("The registration of service provider '" + serviceProviderId + "' is not pending approval.");
    }
}
