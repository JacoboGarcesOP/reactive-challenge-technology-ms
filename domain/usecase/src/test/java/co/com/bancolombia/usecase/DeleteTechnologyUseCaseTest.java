package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTechnologyUseCaseTest {

    private static final Long CAPACITY_ID = 1L;
    private static final Long TECHNOLOGY_ID_1 = 1L;
    private static final Long TECHNOLOGY_ID_2 = 2L;
    private static final Long TECHNOLOGY_ID_3 = 3L;
    private static final String CAPACITY_ID_CANNOT_BE_NULL_MESSAGE = "Capacity ID cannot be null";
    private static final String CAPACITY_NOT_FOUND_MESSAGE = "Capacity has not been found. Capacity id: ";

    @Mock
    private TechnologyGateway gateway;

    private DeleteTechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteTechnologyUseCase(gateway);
    }

    @Test
    void shouldDeleteTechnologiesSuccessfully_whenAllHaveOnlyOneCapacity() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        Technology technology2 = createTechnology(TECHNOLOGY_ID_2, "Spring", "Framework");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1, technology2));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(1L));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_2))
            .thenReturn(Mono.just(1L));
        when(gateway.delete(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(true));
        when(gateway.delete(TECHNOLOGY_ID_2))
            .thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectNext(List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2))
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_2);
        verify(gateway).delete(TECHNOLOGY_ID_1);
        verify(gateway).delete(TECHNOLOGY_ID_2);
        verify(gateway, never()).deleteTechnologyCapacityRelation(anyLong(), anyLong());
    }

    @Test
    void shouldDeleteOnlyRelations_whenTechnologiesHaveMultipleCapacities() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        Technology technology2 = createTechnology(TECHNOLOGY_ID_2, "Spring", "Framework");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1, technology2));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(2L));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_2))
            .thenReturn(Mono.just(3L));
        when(gateway.deleteTechnologyCapacityRelation(TECHNOLOGY_ID_1, CAPACITY_ID))
            .thenReturn(Mono.just(true));
        when(gateway.deleteTechnologyCapacityRelation(TECHNOLOGY_ID_2, CAPACITY_ID))
            .thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectNext(List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2))
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_2);
        verify(gateway).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_1, CAPACITY_ID);
        verify(gateway).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_2, CAPACITY_ID);
        verify(gateway, never()).delete(anyLong());
    }

    @Test
    void shouldMixDeleteAndRelationDeletion_whenSomeHaveOneCapacityAndOthersHaveMultiple() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        Technology technology2 = createTechnology(TECHNOLOGY_ID_2, "Spring", "Framework");
        Technology technology3 = createTechnology(TECHNOLOGY_ID_3, "Hibernate", "ORM");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1, technology2, technology3));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(1L)); // Solo esta capacidad
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_2))
            .thenReturn(Mono.just(2L)); // Múltiples capacidades
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_3))
            .thenReturn(Mono.just(1L)); // Solo esta capacidad
        when(gateway.delete(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(true));
        when(gateway.deleteTechnologyCapacityRelation(TECHNOLOGY_ID_2, CAPACITY_ID))
            .thenReturn(Mono.just(true));
        when(gateway.delete(TECHNOLOGY_ID_3))
            .thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectNext(List.of(TECHNOLOGY_ID_1, TECHNOLOGY_ID_2, TECHNOLOGY_ID_3))
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_2);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_3);
        verify(gateway).delete(TECHNOLOGY_ID_1);
        verify(gateway).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_2, CAPACITY_ID);
        verify(gateway).delete(TECHNOLOGY_ID_3);
        verify(gateway, never()).delete(TECHNOLOGY_ID_2);
        verify(gateway, never()).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_1, CAPACITY_ID);
        verify(gateway, never()).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_3, CAPACITY_ID);
    }

    @Test
    void shouldThrowException_whenCapacityIdIsNull() {
        // When & Then
        StepVerifier.create(useCase.execute(null))
            .expectError(BussinessException.class)
            .verify();

        verify(gateway, never()).findByCapacity(anyLong());
    }

    @Test
    void shouldThrowException_whenCapacityNotFound() {
        // Given
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(BussinessException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldThrowException_whenCapacityIdIsNull_withCorrectMessage() {
        // When & Then
        StepVerifier.create(useCase.execute(null))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals(CAPACITY_ID_CANNOT_BE_NULL_MESSAGE))
            .verify();
    }

    @Test
    void shouldThrowException_whenCapacityNotFound_withCorrectMessage() {
        // Given
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals(CAPACITY_NOT_FOUND_MESSAGE + CAPACITY_ID))
            .verify();
    }

    @Test
    void shouldPropagateError_whenFindByCapacityFails() {
        // Given
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldPropagateError_whenCountCapacitiesByTechnologyIdFails() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.error(new RuntimeException("Count error")));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
    }

    @Test
    void shouldPropagateError_whenDeleteTechnologyFails() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(1L));
        when(gateway.delete(TECHNOLOGY_ID_1))
            .thenReturn(Mono.error(new RuntimeException("Delete error")));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
        verify(gateway).delete(TECHNOLOGY_ID_1);
    }

    @Test
    void shouldPropagateError_whenDeleteTechnologyCapacityRelationFails() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, "Java", "Programming language");
        
        when(gateway.findByCapacity(CAPACITY_ID))
            .thenReturn(Flux.just(technology1));
        when(gateway.countCapacitiesByTechnologyId(TECHNOLOGY_ID_1))
            .thenReturn(Mono.just(2L));
        when(gateway.deleteTechnologyCapacityRelation(TECHNOLOGY_ID_1, CAPACITY_ID))
            .thenReturn(Mono.error(new RuntimeException("Delete relation error")));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
        verify(gateway).countCapacitiesByTechnologyId(TECHNOLOGY_ID_1);
        verify(gateway).deleteTechnologyCapacityRelation(TECHNOLOGY_ID_1, CAPACITY_ID);
    }

    private Technology createTechnology(Long id, String name, String description) {
        return new Technology(id, name, description);
    }
}