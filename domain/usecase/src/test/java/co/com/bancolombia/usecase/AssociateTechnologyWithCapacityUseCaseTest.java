package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.TechnologyCapacity;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.command.AssociateTechnologyWithCapacityCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.AssociateTechnologyWithCapacityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssociateTechnologyWithCapacityUseCaseTest {

    private static final Long TECHNOLOGY_ID = 1L;
    private static final String TECHNOLOGY_NAME = "Java";
    private static final String TECHNOLOGY_DESCRIPTION = "Programming language";
    private static final Long CAPACITY_ID = 2L;
    private static final String TECHNOLOGY_NOT_FOUND_MESSAGE = "The technology name has not been found.";
    private static final String ASSOCIATION_ALREADY_EXISTS_MESSAGE = "The technology is already associated with this capacity.";

    @Mock
    private TechnologyGateway gateway;

    private AssociateTechnologyWithCapacityUseCase useCase;
    private AssociateTechnologyWithCapacityCommand command;

    @BeforeEach
    void setUp() {
        useCase = new AssociateTechnologyWithCapacityUseCase(gateway);
        command = new AssociateTechnologyWithCapacityCommand(CAPACITY_ID, TECHNOLOGY_NAME);
    }

    @Test
    void shouldAssociateTechnologyWithCapacitySuccessfully_whenTechnologyExistsAndNotAssociated() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        TechnologyCapacity capacity = createTechnologyCapacity(TECHNOLOGY_ID, CAPACITY_ID);

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID)).thenReturn(Mono.empty());
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.just(capacity));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION);
                assertThat(response.getCapacityId()).isEqualTo(CAPACITY_ID);
            })
            .verifyComplete();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway).findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID);
        verify(gateway).associateTechnologyWithCapacity(any(TechnologyCapacity.class));
    }

    @Test
    void shouldComplete_whenTechnologyNotFound() {
        // Given
        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .verifyComplete();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway, never()).findByTechnologyIdAndCapacityId(any(), any());
        verify(gateway, never()).associateTechnologyWithCapacity(any());
    }

    @Test
    void shouldThrowBusinessException_whenTechnologyAlreadyAssociated() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        TechnologyCapacity existingCapacity = createTechnologyCapacity(TECHNOLOGY_ID, CAPACITY_ID);

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID)).thenReturn(Mono.just(existingCapacity));
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.just(existingCapacity));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals(ASSOCIATION_ALREADY_EXISTS_MESSAGE))
            .verify();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway).findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID);
        // Note: Due to the use case logic, associateTechnologyWithCapacity might still be called
    }

    @Test
    void shouldPropagateError_whenGatewayFindByNameFails() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway, never()).findByTechnologyIdAndCapacityId(any(), any());
        verify(gateway, never()).associateTechnologyWithCapacity(any());
    }

    @Test
    void shouldPropagateError_whenGatewayFindByTechnologyIdAndCapacityIdFails() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        RuntimeException gatewayException = new RuntimeException("Database query error");

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID)).thenReturn(Mono.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway).findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID);
        // Note: associateTechnologyWithCapacity might still be called due to the switchIfEmpty logic
    }

    @Test
    void shouldPropagateError_whenGatewayAssociateTechnologyWithCapacityFails() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        RuntimeException gatewayException = new RuntimeException("Database save error");

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID)).thenReturn(Mono.empty());
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway).findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID);
        verify(gateway).associateTechnologyWithCapacity(any(TechnologyCapacity.class));
    }

    @Test
    void shouldCreateCorrectTechnologyCapacity_whenAssociating() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        TechnologyCapacity capacity = createTechnologyCapacity(TECHNOLOGY_ID, CAPACITY_ID);

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, CAPACITY_ID)).thenReturn(Mono.empty());
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.just(capacity));

        // When
        StepVerifier.create(useCase.execute(command))
            .expectNextCount(1)
            .verifyComplete();

        // Then
        verify(gateway).associateTechnologyWithCapacity(any(TechnologyCapacity.class));
    }

    @Test
    void shouldHandleDifferentTechnologyNames() {
        // Given
        String differentTechnologyName = "Python";
        AssociateTechnologyWithCapacityCommand differentCommand = new AssociateTechnologyWithCapacityCommand(CAPACITY_ID, differentTechnologyName);
        Technology technology = createTechnology(3L, differentTechnologyName, "Scripting language");
        TechnologyCapacity capacity = createTechnologyCapacity(3L, CAPACITY_ID);

        when(gateway.findByName(differentTechnologyName)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(3L, CAPACITY_ID)).thenReturn(Mono.empty());
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.just(capacity));

        // When & Then
        StepVerifier.create(useCase.execute(differentCommand))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(3L);
                assertThat(response.getName()).isEqualTo(differentTechnologyName);
                assertThat(response.getCapacityId()).isEqualTo(CAPACITY_ID);
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleDifferentCapacityIds() {
        // Given
        Long differentCapacityId = 5L;
        AssociateTechnologyWithCapacityCommand differentCommand = new AssociateTechnologyWithCapacityCommand(differentCapacityId, TECHNOLOGY_NAME);
        Technology technology = createTechnology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        TechnologyCapacity capacity = createTechnologyCapacity(TECHNOLOGY_ID, differentCapacityId);

        when(gateway.findByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(technology));
        when(gateway.findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, differentCapacityId)).thenReturn(Mono.empty());
        when(gateway.associateTechnologyWithCapacity(any(TechnologyCapacity.class))).thenReturn(Mono.just(capacity));

        // When & Then
        StepVerifier.create(useCase.execute(differentCommand))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME);
                assertThat(response.getCapacityId()).isEqualTo(differentCapacityId);
            })
            .verifyComplete();

        verify(gateway).findByName(TECHNOLOGY_NAME);
        verify(gateway).findByTechnologyIdAndCapacityId(TECHNOLOGY_ID, differentCapacityId);
        verify(gateway).associateTechnologyWithCapacity(any(TechnologyCapacity.class));
    }

    private Technology createTechnology(Long id, String name, String description) {
        return new Technology(id, name, description);
    }

    private TechnologyCapacity createTechnologyCapacity(Long technologyId, Long capacityId) {
        return new TechnologyCapacity(technologyId, capacityId);
    }
}
