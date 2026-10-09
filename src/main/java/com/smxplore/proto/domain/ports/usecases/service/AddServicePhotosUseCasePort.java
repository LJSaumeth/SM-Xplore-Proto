package com.smxplore.proto.domain.ports.usecases.service;

import java.util.Collection;
import java.util.UUID;

public interface AddServicePhotosUseCasePort {
    boolean handle(UUID reportId, Collection<String> photos);
}
