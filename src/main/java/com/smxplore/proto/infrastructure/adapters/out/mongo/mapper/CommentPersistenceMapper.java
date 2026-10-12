package com.smxplore.proto.infrastructure.adapters.out.mongo.mapper;

import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.CommentDocument;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.UserRefDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentPersistenceMapper {

    CommentDocument toDocument(Comment comment);

    Comment toDomain(CommentDocument document);

    UserRefDocument toDocument(UserRef userRef);

    UserRef toDomain(UserRefDocument document);
}
