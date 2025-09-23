package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateTechnologyRequest;
import co.com.bancolombia.api.response.ErrorResponse;
import co.com.bancolombia.model.technology.exceptions.DomainException;
import co.com.bancolombia.usecase.CreateTechnologyUseCase;
import co.com.bancolombia.usecase.command.CreateTechnologyCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
  private static final String VALIDATION_ERROR_TEXT = "VALIDATION_ERROR";
  private static final String DOMAIN_ERROR_TEXT = "DOMAIN_ERROR";
  private static final String BUSINESS_ERROR_TEXT = "BUSINESS_ERROR";
  private static final String INTERNAL_ERROR_TEXT = "INTERNAL_ERROR";
  private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

  private final CreateTechnologyUseCase createTechnologyUseCase;
  private final Validator validator;

  public Mono<ServerResponse> createTechnology(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateTechnologyRequest.class)
      .doOnNext(this::validateRequest)
      .map(this::mapToCommand)
      .flatMap(createTechnologyUseCase::execute)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(ConstraintViolationException.class, this::handleValidationException)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error(GENERIC_ERROR_MESSAGE, error));
  }

  private void validateRequest(CreateTechnologyRequest request) {
    Set<ConstraintViolation<CreateTechnologyRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private CreateTechnologyCommand mapToCommand(CreateTechnologyRequest request) {
    return new CreateTechnologyCommand(request.getName(), request.getDescription());
  }

  private Mono<ServerResponse> buildSuccessResponse(Object response) {
    return ServerResponse.ok()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(response);
  }

  private Mono<ServerResponse> handleValidationException(ConstraintViolationException ex) {
    String errorMessage = ex.getConstraintViolations().stream()
      .map(ConstraintViolation::getMessage)
      .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", errorMessage);

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(VALIDATION_ERROR_TEXT, errorMessage));
  }

  private Mono<ServerResponse> handleDomainException(DomainException ex) {
    log.warn("Domain error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(DOMAIN_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleBusinessException(BussinessException ex) {
    log.warn("Business error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(BUSINESS_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(INTERNAL_ERROR_TEXT, GENERIC_ERROR_MESSAGE));
  }

  private ErrorResponse createErrorResponse(String error, String message) {
    return new ErrorResponse(error, message);
  }
}