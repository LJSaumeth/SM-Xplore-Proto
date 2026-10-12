package com.smxplore.proto.infrastructure.adapters.in.web.error;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityCapacityException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityNameException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityPriceException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityRequestException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityScheduleException;
import com.smxplore.proto.domain.exceptions.activity.InvalidActivityStatusTransitionException;
import com.smxplore.proto.domain.exceptions.attribute.AttributeAlreadyAddedException;
import com.smxplore.proto.domain.exceptions.attribute.InvalidAttributeNameException;
import com.smxplore.proto.domain.exceptions.attribute.InvalidAttributeValueException;
import com.smxplore.proto.domain.exceptions.attribute.NullAttributeException;
import com.smxplore.proto.domain.exceptions.event.EventNotFoundException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventCapacityException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventNameException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventPriceException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventRequestException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventScheduleException;
import com.smxplore.proto.domain.exceptions.event.InvalidEventStatusTransitionException;
import com.smxplore.proto.domain.exceptions.event.InvalidExpectedAttendanceException;
import com.smxplore.proto.domain.exceptions.location.InvalidLocationParamsException;
import com.smxplore.proto.domain.exceptions.securityreport.InvalidCommentException;
import com.smxplore.proto.domain.exceptions.securityreport.InvalidReportRequestException;
import com.smxplore.proto.domain.exceptions.securityreport.InvalidStatusTransitionException;
import com.smxplore.proto.domain.exceptions.securityreport.InvalidUserReporterException;
import com.smxplore.proto.domain.exceptions.securityreport.InvalidUserReporterNameException;
import com.smxplore.proto.domain.exceptions.securityreport.NoCommentContentException;
import com.smxplore.proto.domain.exceptions.securityreport.NoUserReporterException;
import com.smxplore.proto.domain.exceptions.securityreport.ReportNotFoundException;
import com.smxplore.proto.domain.exceptions.securityreport.ReportWithoutContentException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceNameException;
import com.smxplore.proto.domain.exceptions.service.InvalidServicePriceException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderNameException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceRequestException;
import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.exceptions.service.TooManyExtraAttributeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ReportNotFoundException.class, ServiceNotFoundException.class,
            EventNotFoundException.class, ActivityNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), exchange);
    }

    @ExceptionHandler({InvalidStatusTransitionException.class, AttributeAlreadyAddedException.class,
            InvalidEventStatusTransitionException.class, InvalidActivityStatusTransitionException.class})
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException exception, ServerWebExchange exchange) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), exchange);
    }

    @ExceptionHandler({
            InvalidAttributeNameException.class,
            InvalidAttributeValueException.class,
            NullAttributeException.class,
            TooManyExtraAttributeException.class,
            InvalidLocationParamsException.class,
            InvalidReportRequestException.class,
            ReportWithoutContentException.class,
            InvalidUserReporterNameException.class,
            InvalidUserReporterException.class,
            InvalidCommentException.class,
            NoCommentContentException.class,
            NoUserReporterException.class,
            InvalidServiceRequestException.class,
            InvalidServiceNameException.class,
            InvalidServicePriceException.class,
            InvalidServiceProviderException.class,
            InvalidServiceProviderNameException.class,
            InvalidEventRequestException.class,
            InvalidEventNameException.class,
            InvalidEventPriceException.class,
            InvalidEventCapacityException.class,
            InvalidEventScheduleException.class,
            InvalidExpectedAttendanceException.class,
            InvalidActivityRequestException.class,
            InvalidActivityNameException.class,
            InvalidActivityPriceException.class,
            InvalidActivityCapacityException.class,
            InvalidActivityScheduleException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException exception, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(WebExchangeBindException exception,
                                                                 ServerWebExchange exchange) {
        var message = exception.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, message, exchange);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(ServerWebInputException exception,
                                                                 ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, exception.getReason(), exchange);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException exception,
                                                                 ServerWebExchange exchange) {
        return build(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, ServerWebExchange exchange) {
        log.error("Unexpected error handling {}", exchange.getRequest().getPath(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", exchange);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, ServerWebExchange exchange) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(status, message, exchange));
    }
}
