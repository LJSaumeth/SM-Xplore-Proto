package com.smxplore.proto;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.domain.ports.repository.CommentRepositoryPort;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ServiceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MongoAdaptersIntegrationTest {

    @Autowired
    private ServiceRepositoryPort serviceRepository;

    @Autowired
    private SecurityReportRepositoryPort securityReportRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void serviceRoundTripKeepsNestedExtrasAndDecimalPrice() {
        var provider = new ProviderRef(UUID.randomUUID(), "Ana");
        var service = Service.builder()
                .name("Airport transport")
                .type(ServiceType.TRANSPORT)
                .provider(provider)
                .location(new Location(4.65, -74.05))
                .price(new BigDecimal("120000.50"))
                .build();
        service.addExtra(Attribute.builder().name("capacity").value(4).build());
        service.addExtra(Attribute.builder().name("vehicle").value(
                Attribute.builder().name("brand").value("Toyota").build()).build());

        var saved = serviceRepository.save(service).block();
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        StepVerifier.create(serviceRepository.findById(saved.getId()))
                .assertNext(found -> {
                    assertThat(found.getStatus()).isEqualTo(ServiceStatus.AVAILABLE);
                    assertThat(found.getCreatedAt()).isNotNull();
                    assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("120000.50"));
                    assertThat(found.getExtras().size()).isEqualTo(2);

                    var vehicle = found.getExtras().get("vehicle").orElseThrow();
                    assertThat(vehicle.value()).isInstanceOf(Attribute.class);
                    var brand = (Attribute) vehicle.value();
                    assertThat(brand.name()).isEqualTo("brand");
                    assertThat(brand.value()).isEqualTo("Toyota");
                })
                .verifyComplete();

        var nearby = mongoTemplate.find(
                        Query.query(Criteria.where("location").nearSphere(new GeoJsonPoint(-74.05, 4.65))),
                        ServiceDocument.class)
                .map(ServiceDocument::id)
                .collectList()
                .block();
        assertThat(nearby).contains(saved.getId());

        var indexNames = mongoTemplate.indexOps(ServiceDocument.class).getIndexInfo()
                .map(IndexInfo::getName)
                .collectList()
                .block();
        assertThat(indexNames).anyMatch(name -> name.toLowerCase().contains("location"));
    }

    @Test
    void securityReportRoundTripAndPartialUpdates() {
        var user = new UserRef(UUID.randomUUID(), "Nestor");
        var report = SecurityReport.builder()
                .user(user)
                .text("Robo en el parque")
                .location(new Location(4.60, -74.10))
                .build();

        var saved = securityReportRepository.save(report).block();
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(SecurityReportStatus.ONGOING);
        assertThat(saved.getCreatedAt()).isNotNull();

        securityReportRepository.changeText(saved.getId(), "Robo cerca del parque").block();
        securityReportRepository.changeStatus(saved.getId(), SecurityReportStatus.REPORTED).block();

        var updated = securityReportRepository.findById(saved.getId()).block();
        assertThat(updated).isNotNull();
        assertThat(updated.getText()).isEqualTo("Robo cerca del parque");
        assertThat(updated.getStatus()).isEqualTo(SecurityReportStatus.REPORTED);

        var byUser = securityReportRepository.findAllByUserId(user.id()).collectList().block();
        assertThat(byUser).extracting(SecurityReport::getId).contains(saved.getId());

        var byStatus = securityReportRepository.findAllByStatus(SecurityReportStatus.REPORTED).collectList().block();
        assertThat(byStatus).extracting(SecurityReport::getId).contains(saved.getId());
    }

    @Test
    void commentsAreStoredByReport() {
        var report = securityReportRepository.save(SecurityReport.builder()
                .user(new UserRef(UUID.randomUUID(), "Nestor"))
                .text("Reporte con comentarios")
                .location(new Location(4.6, -74.1))
                .build()).block();

        var comment = Comment.builder()
                .reportId(report.getId())
                .user(new UserRef(UUID.randomUUID(), "Pedro"))
                .content("Yo lo vi")
                .build();

        assertThat(comment.getId()).isNotNull();

        StepVerifier.create(commentRepository.save(comment)
                        .flatMapMany(saved -> commentRepository.findAllByReportId(saved.getReportId())))
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(comment.getId());
                    assertThat(found.getContent()).isEqualTo("Yo lo vi");
                    assertThat(found.getReportId()).isEqualTo(report.getId());
                })
                .verifyComplete();
    }
}
