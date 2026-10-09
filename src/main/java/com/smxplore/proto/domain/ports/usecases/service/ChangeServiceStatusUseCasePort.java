package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.ServiceStatus;

import java.util.UUID;

public interface ChangeServiceStatusUseCasePort {
    boolean handle(UUID serviceId, ServiceStatus newStatus);
}
