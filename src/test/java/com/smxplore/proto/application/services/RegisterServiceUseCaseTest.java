package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RegisterServiceUseCaseTest {

    @Mock
    private ServiceRepositoryPort serviceRepository;

    @InjectMocks
    private RegisterServiceUseCase useCase;

    @Test
    void assignsTheProviderBeforeValidating() {
        var provider = new ProviderRef(UUID.randomUUID(), "Ana");
        var service = Service.builder()
                .name("Airport transport")
                .type(ServiceType.TRANSPORT)
                .location(new Location(4.65, -74.05))
                .price(new BigDecimal("45000.50"))
                .build();
        given(serviceRepository.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.handle(provider, service))
                .assertNext(registered -> {
                    assertThat(registered.getProvider()).isEqualTo(provider);
                    assertThat(registered.getId()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void failsWhenTheServiceHasNoProvider() {
        var service = Service.builder()
                .name("Airport transport")
                .type(ServiceType.TRANSPORT)
                .location(new Location(4.65, -74.05))
                .price(new BigDecimal("45000.50"))
                .build();

        StepVerifier.create(useCase.handle(null, service))
                .expectError(InvalidServiceProviderException.class)
                .verify();
    }
}
