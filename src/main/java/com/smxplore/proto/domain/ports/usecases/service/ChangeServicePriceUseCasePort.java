package com.smxplore.proto.domain.ports.usecases.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface ChangeServicePriceUseCasePort {
    boolean handle(UUID serviceId, BigDecimal newPrice);
}
