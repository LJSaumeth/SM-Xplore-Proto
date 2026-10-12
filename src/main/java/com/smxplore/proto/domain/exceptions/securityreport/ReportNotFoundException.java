package com.smxplore.proto.domain.exceptions.securityreport;

import java.util.UUID;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(UUID reportId) {
        super("Report with id %s not found".formatted(reportId.toString()));
    }
}
