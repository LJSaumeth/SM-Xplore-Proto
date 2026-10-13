package com.smxplore.proto.application.zones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

class ZoneUseCasesTest {

    private final IZoneRepository zoneRepository = mock(IZoneRepository.class);

    private final RegisterZoneUseCase registerZoneUseCase = new RegisterZoneUseCase(zoneRepository);
    private final RenameZoneUseCase renameZoneUseCase = new RenameZoneUseCase(zoneRepository);
    private final FindZoneUseCase findZoneUseCase = new FindZoneUseCase(zoneRepository);
    private final ListZonesUseCase listZonesUseCase = new ListZonesUseCase(zoneRepository);

    private Zone zone(String name) {
        return new Zone(UUID.randomUUID(), name);
    }

    @Test
    void shouldRegisterAZone() {
        when(zoneRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerZoneUseCase.handle("Historic Center"))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getName()).isEqualTo("Historic Center");
                })
                .verifyComplete();
    }

    @Test
    void shouldRenameAZone() {
        var zone = zone("Old Name");
        when(zoneRepository.findById(zone.getId())).thenReturn(Mono.just(zone));
        when(zoneRepository.save(zone)).thenReturn(Mono.just(zone));

        StepVerifier.create(renameZoneUseCase.handle(zone.getId(), "New Name"))
                .assertNext(renamed -> assertThat(renamed.getName()).isEqualTo("New Name"))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenRenamingAMissingZone() {
        var id = UUID.randomUUID();
        when(zoneRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(renameZoneUseCase.handle(id, "New Name"))
                .expectError(ZoneNotFoundException.class)
                .verify();

        verify(zoneRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheZoneDoesNotExist() {
        var id = UUID.randomUUID();
        when(zoneRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(findZoneUseCase.handle(id))
                .expectError(ZoneNotFoundException.class)
                .verify();
    }

    @Test
    void shouldListZones() {
        when(zoneRepository.findAll()).thenReturn(Flux.just(zone("A"), zone("B")));

        StepVerifier.create(listZonesUseCase.handle())
                .expectNextCount(2)
                .verifyComplete();
    }
}
