package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddAttributesRequest(
        @NotEmpty List<@Valid AttributeDto> attributes
) {
}
