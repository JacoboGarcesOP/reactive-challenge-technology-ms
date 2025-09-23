package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateTechnologyRequest;
import co.com.bancolombia.model.technology.exceptions.DomainException;
import co.com.bancolombia.usecase.CreateTechnologyUseCase;
import co.com.bancolombia.usecase.command.CreateTechnologyCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CreateTechnologyResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

  @Mock
  private CreateTechnologyUseCase createTechnologyUseCase;

  @Mock
  private Validator validator;

  @InjectMocks
  private Handler handler;

  private WebTestClient webTestClient;
  private RouterFunction<ServerResponse> routerFunction;

  @BeforeEach
  void setUp() {
    RouterRest routerRest = new RouterRest();
    routerFunction = routerRest.routerFunction(handler);

    webTestClient = WebTestClient
      .bindToRouterFunction(routerFunction)
      .build();
  }

  @Test
  @DisplayName("Debe crear tecnología exitosamente")
  void shouldCreateTechnologySuccessfully() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("Java", "Lenguaje de programación");
    CreateTechnologyResponse expectedResponse = new CreateTechnologyResponse(1L, "Java", "Lenguaje de programación", "Technology created successfuly");

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(Collections.emptySet());
    when(createTechnologyUseCase.execute(any(CreateTechnologyCommand.class)))
      .thenReturn(Mono.just(expectedResponse));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.technologyId").isEqualTo("1")
      .jsonPath("$.name").isEqualTo("Java")
      .jsonPath("$.description").isEqualTo("Lenguaje de programación");

    verify(validator).validate(any(CreateTechnologyRequest.class));
    verify(createTechnologyUseCase).execute(any(CreateTechnologyCommand.class));
  }

  @Test
  @DisplayName("Debe manejar errores de validación")
  void shouldHandleValidationErrors() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("", "");

    Set<ConstraintViolation<CreateTechnologyRequest>> violations = new HashSet<>();
    ConstraintViolation<CreateTechnologyRequest> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Name cannot be empty");
    violations.add(violation);

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(violations);

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("VALIDATION_ERROR")
      .jsonPath("$.message").isEqualTo("Name cannot be empty");

    verify(validator).validate(any(CreateTechnologyRequest.class));
    verifyNoInteractions(createTechnologyUseCase);
  }

  @Test
  @DisplayName("Debe manejar errores de dominio")
  void shouldHandleDomainErrors() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("Java", "Lenguaje de programación");

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(Collections.emptySet());
    when(createTechnologyUseCase.execute(any(CreateTechnologyCommand.class)))
      .thenReturn(Mono.error(new DomainException("Technology already exists")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("DOMAIN_ERROR")
      .jsonPath("$.message").isEqualTo("Technology already exists");

    verify(createTechnologyUseCase).execute(any(CreateTechnologyCommand.class));
  }

  @Test
  @DisplayName("Debe manejar errores de negocio")
  void shouldHandleBusinessErrors() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("Java", "Lenguaje de programación");

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(Collections.emptySet());
    when(createTechnologyUseCase.execute(any(CreateTechnologyCommand.class)))
      .thenReturn(Mono.error(new BussinessException("Business rule violated")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("BUSINESS_ERROR")
      .jsonPath("$.message").isEqualTo("Business rule violated");
  }

  @Test
  @DisplayName("Debe manejar errores genéricos")
  void shouldHandleGenericErrors() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("Java", "Lenguaje de programación");

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(Collections.emptySet());
    when(createTechnologyUseCase.execute(any(CreateTechnologyCommand.class)))
      .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
      .jsonPath("$.message").isEqualTo("An unexpected error occurred");
  }

  @Test
  @DisplayName("Debe manejar cuerpo de petición inválido")
  void shouldHandleInvalidRequestBody() {
    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{invalid json}")
      .exchange()
      .expectStatus().is5xxServerError();
  }

  @Test
  @DisplayName("Debe manejar múltiples violaciones de validación")
  void shouldHandleMultipleValidationViolations() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("", "");

    Set<ConstraintViolation<CreateTechnologyRequest>> violations = new HashSet<>();

    ConstraintViolation<CreateTechnologyRequest> violation1 = mock(ConstraintViolation.class);
    when(violation1.getMessage()).thenReturn("Name cannot be empty");

    ConstraintViolation<CreateTechnologyRequest> violation2 = mock(ConstraintViolation.class);
    when(violation2.getMessage()).thenReturn("Description cannot be empty");

    violations.add(violation1);
    violations.add(violation2);

    when(validator.validate(any(CreateTechnologyRequest.class)))
      .thenReturn(violations);

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("VALIDATION_ERROR")
      .jsonPath("$.message").value(containsString("Name cannot be empty"))
      .jsonPath("$.message").value(containsString("Description cannot be empty"));
  }
}
