package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

public interface FindServicesByStatusUseCasePort {
    Page<Service> handle(ServiceStatus status, PageRequest request);
}
