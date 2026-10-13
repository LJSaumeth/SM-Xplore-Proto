package com.smxplore.proto.infrastructure.adapters.in.web.error;

import com.smxplore.proto.domain.exceptions.InvalidSiteException;
import com.smxplore.proto.domain.exceptions.InvalidZoneException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void siteValidationErrorsMapToBadRequest() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/sites/1").build());

        var response = handler.handleBadRequest(
                new InvalidSiteException("Site latitude must be between -90 and 90."), exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Site latitude must be between -90 and 90.");
    }

    @Test
    void zoneValidationErrorsMapToBadRequest() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/zones/1").build());

        var response = handler.handleBadRequest(
                new InvalidZoneException("Zone name must not be blank."), exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Zone name must not be blank.");
    }
}
