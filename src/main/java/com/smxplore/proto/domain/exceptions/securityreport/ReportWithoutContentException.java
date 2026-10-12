package com.smxplore.proto.domain.exceptions.securityreport;

public class ReportWithoutContentException extends RuntimeException {
    public ReportWithoutContentException(String message) {
        super(message);
    }
}
