package com.smxplore.proto.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.GuideRegistrationNotPendingException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.GuideRepositoryPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class ApproveGuideRegistrationUseCaseTest {

    private final GuideRepositoryPort guideRepository = mock(GuideRepositoryPort.class);
    private final ApproveGuideRegistrationUseCase useCase = new ApproveGuideRegistrationUseCase(guideRepository);

    private Guide guide(UserStatus status) {
        return Guide.builder()
                .id(UUID.randomUUID())
                .fullName("Pending Guide")
                .email("guide@example.com")
                .phone("+573001112233")
                .role(UserRole.GUIDE)
                .status(status)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .license("LIC-123")
                .build();
    }

    @Test
    void shouldApprovePendingRegistrationAndActivateTheAccount() {
        var guide = guide(UserStatus.INACTIVE);
        when(guideRepository.findById(guide.getId())).thenReturn(Mono.just(guide));
        when(guideRepository.save(guide)).thenReturn(Mono.just(guide));

        StepVerifier.create(useCase.handle(guide.getId()))
                .assertNext(approved -> assertThat(approved.getStatus()).isEqualTo(UserStatus.ACTIVE))
                .verifyComplete();

        verify(guideRepository).save(guide);
    }

    @Test
    void shouldFailWhenTheGuideDoesNotExist() {
        var id = UUID.randomUUID();
        when(guideRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(id))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(guideRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheRegistrationIsNotPending() {
        var guide = guide(UserStatus.ACTIVE);
        when(guideRepository.findById(guide.getId())).thenReturn(Mono.just(guide));

        StepVerifier.create(useCase.handle(guide.getId()))
                .expectError(GuideRegistrationNotPendingException.class)
                .verify();

        verify(guideRepository, never()).save(any());
    }
}
