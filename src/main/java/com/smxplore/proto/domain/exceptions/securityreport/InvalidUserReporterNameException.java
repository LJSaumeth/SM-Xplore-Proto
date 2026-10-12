package com.smxplore.proto.domain.exceptions.securityreport;

public class InvalidUserReporterNameException extends RuntimeException {
    public InvalidUserReporterNameException(String message) {
        super(message);
    }
}
