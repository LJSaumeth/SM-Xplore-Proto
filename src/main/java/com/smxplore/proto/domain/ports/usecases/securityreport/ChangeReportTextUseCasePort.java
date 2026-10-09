package com.smxplore.proto.domain.ports.usecases.securityreport;

import java.util.UUID;

public interface ChangeReportTextUseCasePort {
    boolean handle(UUID reportId, String newText);
}
