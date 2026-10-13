package com.smxplore.proto.infrastructure.adapters.out.mongo;

import com.smxplore.proto.infrastructure.adapters.out.mongo.document.CommentDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SecurityReportDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ServiceDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SiteDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ensures the indexes declared on the documents ({@code @Indexed},
 * {@code @CompoundIndex}, {@code @GeoSpatialIndexed}) exist at startup.
 * It replaces {@code spring.data.mongodb.auto-index-creation} so index
 * management is explicit and identical in every environment. Creating an
 * index that already exists with the same definition is a no-op in MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexInitializer implements ApplicationRunner {

    private static final List<Class<?>> INDEXED_DOCUMENTS = List.of(
            SecurityReportDocument.class,
            ServiceDocument.class,
            CommentDocument.class,
            SiteDocument.class
    );

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        var mappingContext = mongoTemplate.getConverter().getMappingContext();
        var resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        for (var documentType : INDEXED_DOCUMENTS) {
            var operations = mongoTemplate.indexOps(documentType);
            var entity = mappingContext.getRequiredPersistentEntity(documentType);
            for (var index : resolver.resolveIndexForEntity(entity)) {
                operations.createIndex(index)
                        .doOnNext(name -> log.info("Index '{}' ensured for {}", name, documentType.getSimpleName()))
                        .block();
            }
        }
    }
}
