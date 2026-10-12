package com.smxplore.proto.domain.exceptions.securityreport;

public class InvalidReportRequestException extends RuntimeException {
    public InvalidReportRequestException(String message) {
        super(message);
    }
}
