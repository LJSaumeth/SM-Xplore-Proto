package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Page;
import com.smxplore.proto.domain.model.types.PageRequest;

public interface FindServicesByTypeUseCasePort {
    Page<Service> findAllByType(ServiceType type, ServiceStatus status, PageRequest request);
}
