package com.smxplore.proto.domain.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User with id '" + userId + "' was not found.");
    }
}
