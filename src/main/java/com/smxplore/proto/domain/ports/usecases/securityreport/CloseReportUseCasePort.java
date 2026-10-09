package com.smxplore.proto.domain.ports.usecases.securityreport;

import java.util.UUID;

public interface CloseReportUseCasePort {
    boolean handle(UUID securityReportId);
}
