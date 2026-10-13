package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class DestinationManagerRegistrationNotPendingException extends RuntimeException {

    public DestinationManagerRegistrationNotPendingException(UUID destinationManagerId) {
        super("The registration of destination manager '" + destinationManagerId + "' is not pending approval.");
    }
}
