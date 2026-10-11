package com.smxplore.proto.domain.exceptions.activity;

import java.io.Serial;

public class InvalidActivityNameException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidActivityNameException(String message) {
        super(message);
    }
}
