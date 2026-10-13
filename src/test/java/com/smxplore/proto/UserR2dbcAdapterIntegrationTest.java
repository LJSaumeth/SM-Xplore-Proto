package com.smxplore.proto;

import static org.assertj.core.api.Assertions.assertThat;

import com.smxplore.proto.domain.model.serviceprovider.RegistrationStatus;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.TouristRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserR2dbcAdapterIntegrationTest {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private TouristRepositoryPort touristRepository;

    @Test
    void touristRoundTripKeepsUserAndSubtypeData() {
        var tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("Ana Gomez")
                .email("ana.gomez@example.com")
                .phone("+573001112233")
                .passwordHash("hashed")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("100200300")
                .dniType(DniType.CITIZENSHIP_CARD)
                .preferredLanguage("es")
                .nationality("Colombian")
                .build();
        tourist.validate();

        var saved = userRepository.save(tourist).block();
        assertThat(saved).isNotNull();

        var found = touristRepository.findById(saved.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getFullName()).isEqualTo("Ana Gomez");
        assertThat(found.getEmail()).isEqualTo("ana.gomez@example.com");
        assertThat(found.getDniType()).isEqualTo(DniType.CITIZENSHIP_CARD);
        assertThat(found.getRole()).isEqualTo(UserRole.TOURIST);

        var byEmail = userRepository.findByEmail("ana.gomez@example.com").block();
        assertThat(byEmail).isInstanceOf(Tourist.class);

        var searched = userRepository
                .search(new UserSearchCriteria(UserRole.TOURIST, null, "Ana"))
                .collectList()
                .block();
        assertThat(searched).isNotNull().isNotEmpty();
    }

    @Test
    void serviceProviderRoundTripKeepsRegistrationStatus() {
        var provider = ServiceProvider.builder()
                .id(UUID.randomUUID())
                .fullName("Kayak Tours")
                .email("contact@kayak.com")
                .phone("+573004445566")
                .passwordHash("hashed")
                .role(UserRole.SERVICE_PROVIDER)
                .status(UserStatus.INACTIVE)
                .signedUpAt(Instant.now())
                .nit("900123456")
                .serviceType("Nautical")
                .registrationStatus(RegistrationStatus.PENDING)
                .latitude(11.24)
                .longitude(-74.19)
                .build();
        provider.validate();

        userRepository.save(provider).block();

        var found = (ServiceProvider) userRepository.findById(provider.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getRegistrationStatus()).isEqualTo(RegistrationStatus.PENDING);
        assertThat(found.getNit()).isEqualTo("900123456");
    }

    @Test
    void emailLookupsAreCaseInsensitive() {
        var tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("Case Test")
                .email("case.test@example.com")
                .phone("+573009998877")
                .passwordHash("hashed")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("998877665")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();
        tourist.validate();
        userRepository.save(tourist).block();

        // Any casing of the input must find the account (login relies on this)
        var byEmail = userRepository.findByEmail("Case.Test@Example.com").block();
        assertThat(byEmail).isNotNull();
        assertThat(byEmail.getEmail()).isEqualTo("case.test@example.com");

        var exists = userRepository.existsByEmail("CASE.TEST@EXAMPLE.COM").block();
        assertThat(exists).isTrue();
    }
}
