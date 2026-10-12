package com.smxplore.proto.infrastructure.adapters.out.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "comments")
public record CommentDocument(
        @Id
        UUID id,
        @Indexed
        UUID reportId,
        UserRefDocument user,
        String content
) {
}
