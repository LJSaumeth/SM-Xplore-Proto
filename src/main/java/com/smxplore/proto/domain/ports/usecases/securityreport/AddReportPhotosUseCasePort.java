package com.smxplore.proto.domain.ports.usecases.securityreport;

import java.util.Collection;
import java.util.UUID;

public interface AddReportPhotosUseCasePort {
    boolean handle(UUID reportId, Collection<String> photos);
}
