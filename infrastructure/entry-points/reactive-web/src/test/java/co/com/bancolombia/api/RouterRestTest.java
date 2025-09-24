package co.com.bancolombia.api;

import co.com.bancolombia.api.request.AssociateTechnologyWithCapacityRequest;
import co.com.bancolombia.api.request.CreateTechnologyRequest;
import co.com.bancolombia.model.technology.exceptions.DomainException;
import co.com.bancolombia.usecase.AssociateTechnologyWithCapacityUseCase;
import co.com.bancolombia.usecase.CreateTechnologyUseCase;
import co.com.bancolombia.usecase.FindAllTechnologiesUseCase;
import co.com.bancolombia.usecase.FindTechnologiesByCapacityUseCase;
import co.com.bancolombia.usecase.command.AssociateTechnologyWithCapacityCommand;
import co.com.bancolombia.usecase.command.CreateTechnologyCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.AssociateTechnologyWithCapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

  @Mock
  private CreateTechnologyUseCase createTechnologyUseCase;

  @Mock
  private FindAllTechnologiesUseCase findAllTechnologiesUseCase;

  @Mock
  private FindTechnologiesByCapacityUseCase findTechnologiesByCapacityUseCase;

  @Mock
  private AssociateTechnologyWithCapacityUseCase associateTechnologyWithCapacityUseCase;

  @Mock
  private Validator validator;

  @InjectMocks
  private Handler handler;

  private WebTestClient webTestClient;
  private RouterFunction<ServerResponse> routerFunction;

  @BeforeEach
  void setUp() {
    RouterRest routerRest = new RouterRest();
    routerFunction = (RouterFunction<ServerResponse>) routerRest.createTechnologyRouter(handler)
      .andOther(routerRest.findAllTechnologiesRouter(handler))
      .andOther(routerRest.findTechnologiesByCapacityRouter(handler))
      .andOther(routerRest.associateTechnologyWithCapacityRouter(handler));

    webTestClient = WebTestClient
      .bindToRouterFunction(routerFunction)
      .build();
  }

  @Test
  @DisplayName("Debe crear tecnología exitosamente")
  void shouldCreateTechnologySuccessfully() {
    // Given
    CreateTechnologyRequest request = new CreateTechnologyRequest("Java", "Lenguaje de programación");
    TechnologyResponse expectedResponse = new TechnologyResponse(1L, "Java", "Lenguaje de programación");

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

  // ========== TESTS FOR FIND ALL TECHNOLOGIES ==========

  @Test
  @DisplayName("Debe obtener todas las tecnologías exitosamente")
  void shouldFindAllTechnologiesSuccessfully() {
    // Given
    TechnologyResponse tech1 = new TechnologyResponse(1L, "Java", "Lenguaje de programación");
    TechnologyResponse tech2 = new TechnologyResponse(2L, "Spring Boot", "Framework de Java");
    List<TechnologyResponse> expectedResponse = List.of(tech1, tech2);

    when(findAllTechnologiesUseCase.execute())
      .thenReturn(Flux.fromIterable(expectedResponse));

    // When & Then
    webTestClient
      .get()
      .uri("/v1/api/technology")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$[0].technologyId").isEqualTo("1")
      .jsonPath("$[0].name").isEqualTo("Java")
      .jsonPath("$[0].description").isEqualTo("Lenguaje de programación")
      .jsonPath("$[1].technologyId").isEqualTo("2")
      .jsonPath("$[1].name").isEqualTo("Spring Boot")
      .jsonPath("$[1].description").isEqualTo("Framework de Java");

    verify(findAllTechnologiesUseCase).execute();
  }

  @Test
  @DisplayName("Debe manejar errores al obtener todas las tecnologías")
  void shouldHandleErrorsWhenFindingAllTechnologies() {
    // Given
    when(findAllTechnologiesUseCase.execute())
      .thenReturn(Flux.error(new RuntimeException("Database error")));

    // When & Then
    webTestClient
      .get()
      .uri("/v1/api/technology")
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
      .jsonPath("$.message").isEqualTo("An unexpected error occurred");

    verify(findAllTechnologiesUseCase).execute();
  }

  // ========== TESTS FOR FIND TECHNOLOGIES BY CAPACITY ==========

  @Test
  @DisplayName("Debe obtener tecnologías por capacidad exitosamente")
  void shouldFindTechnologiesByCapacitySuccessfully() {
    // Given
    Long capacityId = 1L;
    TechnologyResponse tech1 = new TechnologyResponse(1L, "Java", "Lenguaje de programación");
    TechnologyResponse tech2 = new TechnologyResponse(2L, "Spring Boot", "Framework de Java");
    List<TechnologyResponse> expectedResponse = List.of(tech1, tech2);

    when(findTechnologiesByCapacityUseCase.execute(capacityId))
      .thenReturn(Flux.fromIterable(expectedResponse));

    // When & Then
    webTestClient
      .get()
      .uri("/v1/api/technology/capacity/{capacityId}", capacityId)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$[0].technologyId").isEqualTo("1")
      .jsonPath("$[0].name").isEqualTo("Java")
      .jsonPath("$[0].description").isEqualTo("Lenguaje de programación")
      .jsonPath("$[1].technologyId").isEqualTo("2")
      .jsonPath("$[1].name").isEqualTo("Spring Boot")
      .jsonPath("$[1].description").isEqualTo("Framework de Java");

    verify(findTechnologiesByCapacityUseCase).execute(capacityId);
  }

  @Test
  @DisplayName("Debe manejar errores de negocio al obtener tecnologías por capacidad")
  void shouldHandleBusinessErrorsWhenFindingTechnologiesByCapacity() {
    // Given
    Long capacityId = 999L;

    when(findTechnologiesByCapacityUseCase.execute(capacityId))
      .thenReturn(Flux.error(new BussinessException("Capacity not found")));

    // When & Then
    webTestClient
      .get()
      .uri("/v1/api/technology/capacity/{capacityId}", capacityId)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("BUSINESS_ERROR")
      .jsonPath("$.message").isEqualTo("Capacity not found");

    verify(findTechnologiesByCapacityUseCase).execute(capacityId);
  }

  @Test
  @DisplayName("Debe manejar errores genéricos al obtener tecnologías por capacidad")
  void shouldHandleGenericErrorsWhenFindingTechnologiesByCapacity() {
    // Given
    Long capacityId = 1L;

    when(findTechnologiesByCapacityUseCase.execute(capacityId))
      .thenReturn(Flux.error(new RuntimeException("Database connection failed")));

    // When & Then
    webTestClient
      .get()
      .uri("/v1/api/technology/capacity/{capacityId}", capacityId)
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
      .jsonPath("$.message").isEqualTo("An unexpected error occurred");

    verify(findTechnologiesByCapacityUseCase).execute(capacityId);
  }

  // ========== TESTS FOR ASSOCIATE TECHNOLOGY WITH CAPACITY ==========

  @Test
  @DisplayName("Debe asociar tecnología con capacidad exitosamente")
  void shouldAssociateTechnologyWithCapacitySuccessfully() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(1L, "Java");
    AssociateTechnologyWithCapacityResponse expectedResponse = new AssociateTechnologyWithCapacityResponse(1L, "Java", "Lenguaje de programación", 1L);

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(Collections.emptySet());
    when(associateTechnologyWithCapacityUseCase.execute(any(AssociateTechnologyWithCapacityCommand.class)))
      .thenReturn(Mono.just(expectedResponse));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.technologyId").isEqualTo("1")
      .jsonPath("$.name").isEqualTo("Java")
      .jsonPath("$.description").isEqualTo("Lenguaje de programación")
      .jsonPath("$.capacityId").isEqualTo("1");

    verify(validator).validate(any(AssociateTechnologyWithCapacityRequest.class));
    verify(associateTechnologyWithCapacityUseCase).execute(any(AssociateTechnologyWithCapacityCommand.class));
  }

  @Test
  @DisplayName("Debe manejar errores de validación al asociar tecnología con capacidad")
  void shouldHandleValidationErrorsWhenAssociatingTechnologyWithCapacity() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(null, "");

    Set<ConstraintViolation<AssociateTechnologyWithCapacityRequest>> violations = new HashSet<>();
    ConstraintViolation<AssociateTechnologyWithCapacityRequest> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Capacity id is required");
    violations.add(violation);

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(violations);

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("VALIDATION_ERROR")
      .jsonPath("$.message").isEqualTo("Capacity id is required");

    verify(validator).validate(any(AssociateTechnologyWithCapacityRequest.class));
    verifyNoInteractions(associateTechnologyWithCapacityUseCase);
  }

  @Test
  @DisplayName("Debe manejar errores de negocio al asociar tecnología con capacidad")
  void shouldHandleBusinessErrorsWhenAssociatingTechnologyWithCapacity() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(1L, "NonExistentTech");

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(Collections.emptySet());
    when(associateTechnologyWithCapacityUseCase.execute(any(AssociateTechnologyWithCapacityCommand.class)))
      .thenReturn(Mono.error(new BussinessException("The technology name has not been found.")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("BUSINESS_ERROR")
      .jsonPath("$.message").isEqualTo("The technology name has not been found.");

    verify(associateTechnologyWithCapacityUseCase).execute(any(AssociateTechnologyWithCapacityCommand.class));
  }

  @Test
  @DisplayName("Debe manejar errores de dominio al asociar tecnología con capacidad")
  void shouldHandleDomainErrorsWhenAssociatingTechnologyWithCapacity() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(1L, "Java");

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(Collections.emptySet());
    when(associateTechnologyWithCapacityUseCase.execute(any(AssociateTechnologyWithCapacityCommand.class)))
      .thenReturn(Mono.error(new DomainException("Invalid technology state")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("DOMAIN_ERROR")
      .jsonPath("$.message").isEqualTo("Invalid technology state");

    verify(associateTechnologyWithCapacityUseCase).execute(any(AssociateTechnologyWithCapacityCommand.class));
  }

  @Test
  @DisplayName("Debe manejar errores genéricos al asociar tecnología con capacidad")
  void shouldHandleGenericErrorsWhenAssociatingTechnologyWithCapacity() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(1L, "Java");

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(Collections.emptySet());
    when(associateTechnologyWithCapacityUseCase.execute(any(AssociateTechnologyWithCapacityCommand.class)))
      .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
      .jsonPath("$.message").isEqualTo("An unexpected error occurred");

    verify(associateTechnologyWithCapacityUseCase).execute(any(AssociateTechnologyWithCapacityCommand.class));
  }

  @Test
  @DisplayName("Debe manejar múltiples violaciones de validación al asociar tecnología")
  void shouldHandleMultipleValidationViolationsWhenAssociatingTechnology() {
    // Given
    AssociateTechnologyWithCapacityRequest request = new AssociateTechnologyWithCapacityRequest(null, "");

    Set<ConstraintViolation<AssociateTechnologyWithCapacityRequest>> violations = new HashSet<>();

    ConstraintViolation<AssociateTechnologyWithCapacityRequest> violation1 = mock(ConstraintViolation.class);
    when(violation1.getMessage()).thenReturn("Capacity id is required");

    ConstraintViolation<AssociateTechnologyWithCapacityRequest> violation2 = mock(ConstraintViolation.class);
    when(violation2.getMessage()).thenReturn("Technology name cannot be empty");

    violations.add(violation1);
    violations.add(violation2);

    when(validator.validate(any(AssociateTechnologyWithCapacityRequest.class)))
      .thenReturn(violations);

    // When & Then
    webTestClient
      .post()
      .uri("/v1/api/technology/associate")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("VALIDATION_ERROR")
      .jsonPath("$.message").value(containsString("Capacity id is required"))
      .jsonPath("$.message").value(containsString("Technology name cannot be empty"));

    verify(validator).validate(any(AssociateTechnologyWithCapacityRequest.class));
    verifyNoInteractions(associateTechnologyWithCapacityUseCase);
  }
}
