package com.smxplore.proto.domain.ports.usecases.securityreport;

import java.util.UUID;

public interface GiveLikeToReportUseCasePort {
    void handle(UUID reportId, boolean remove);
}
