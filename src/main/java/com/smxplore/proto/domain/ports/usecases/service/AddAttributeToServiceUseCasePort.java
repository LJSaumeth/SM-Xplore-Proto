package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.types.Attribute;

import java.util.Collection;
import java.util.UUID;

public interface AddAttributeToServiceUseCasePort {
    boolean handle(UUID reportId, Collection<Attribute> attributes);
}
