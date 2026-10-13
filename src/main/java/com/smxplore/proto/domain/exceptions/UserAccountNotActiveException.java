package com.smxplore.proto.domain.exceptions;

import com.smxplore.proto.domain.model.user.UserStatus;

import java.util.UUID;

public class UserAccountNotActiveException extends RuntimeException {

    public UserAccountNotActiveException(UUID userId, UserStatus status) {
        super("The account '" + userId + "' is not active. Current status: '" + status + "'.");
    }
}
