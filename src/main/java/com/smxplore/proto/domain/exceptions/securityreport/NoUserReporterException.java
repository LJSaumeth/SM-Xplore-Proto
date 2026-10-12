package com.smxplore.proto.domain.exceptions.securityreport;

public class NoUserReporterException extends RuntimeException {
    public NoUserReporterException(String message) {
        super(message);
    }
}
