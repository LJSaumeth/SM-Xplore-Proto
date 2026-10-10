package com.smxplore.proto.domain.model.securityreport;

import com.smxplore.proto.domain.exceptions.securityreport.NoCommentContentException;
import com.smxplore.proto.domain.exceptions.securityreport.NoUserReporterException;
import com.smxplore.proto.domain.model.types.UserRef;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder(toBuilder = true)
@Getter
public class Comment {
    private Long id;
    private UUID reportId;
    private UserRef user;
    private String content;

    public void validate() {
        if (user == null)
            throw new NoUserReporterException("No user found");
        user.validate();
        validateContent();
    }

    private  void validateContent() {
        if (content == null || content.isBlank())
            throw new NoCommentContentException("Comment content is empty");
    }
}
