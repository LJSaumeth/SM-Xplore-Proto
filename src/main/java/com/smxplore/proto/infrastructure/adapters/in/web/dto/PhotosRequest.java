package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PhotosRequest(
        @NotEmpty List<String> photosUrls
) {
}
