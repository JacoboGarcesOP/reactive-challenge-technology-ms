package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTechnologiesByCapacityUseCaseTest {

    private static final Long CAPACITY_ID = 1L;
    private static final Long TECHNOLOGY_ID_1 = 1L;
    private static final String TECHNOLOGY_NAME_1 = "Java";
    private static final String TECHNOLOGY_DESCRIPTION_1 = "Programming language";
    
    private static final Long TECHNOLOGY_ID_2 = 2L;
    private static final String TECHNOLOGY_NAME_2 = "Spring Boot";
    private static final String TECHNOLOGY_DESCRIPTION_2 = "Java framework";

    @Mock
    private TechnologyGateway gateway;

    private FindTechnologiesByCapacityUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindTechnologiesByCapacityUseCase(gateway);
    }

    @Test
    void shouldFindTechnologiesByCapacitySuccessfully_whenTechnologiesExist() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        Technology technology2 = createTechnology(TECHNOLOGY_ID_2, TECHNOLOGY_NAME_2, TECHNOLOGY_DESCRIPTION_2);
        List<Technology> technologies = List.of(technology1, technology2);

        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.fromIterable(technologies));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID_1);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME_1);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION_1);
            })
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID_2);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME_2);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION_2);
            })
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldReturnEmptyFlux_whenNoTechnologiesExistForCapacity() {
        // Given
        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldReturnSingleTechnology_whenOnlyOneTechnologyExistsForCapacity() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.just(technology));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID_1);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME_1);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION_1);
            })
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldPropagateError_whenGatewayFindByCapacityFails() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldMapTechnologyToResponseCorrectly() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.just(technology));

        // When
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .assertNext(response -> {
                // Verify mapping is correct
                assertThat(response).isInstanceOf(TechnologyResponse.class);
                assertThat(response.getTechnologyId()).isEqualTo(technology.getId().getValue());
                assertThat(response.getName()).isEqualTo(technology.getName().getValue());
                assertThat(response.getDescription()).isEqualTo(technology.getDescription().getValue());
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleMultipleTechnologiesForCapacityCorrectly() {
        // Given
        Technology technology1 = createTechnology(1L, "Java", "Programming language");
        Technology technology2 = createTechnology(2L, "Python", "Scripting language");
        Technology technology3 = createTechnology(3L, "JavaScript", "Web language");
        
        when(gateway.findByCapacity(CAPACITY_ID)).thenReturn(Flux.just(technology1, technology2, technology3));

        // When & Then
        StepVerifier.create(useCase.execute(CAPACITY_ID))
            .assertNext(response -> assertThat(response.getName()).isEqualTo("Java"))
            .assertNext(response -> assertThat(response.getName()).isEqualTo("Python"))
            .assertNext(response -> assertThat(response.getName()).isEqualTo("JavaScript"))
            .verifyComplete();

        verify(gateway).findByCapacity(CAPACITY_ID);
    }

    @Test
    void shouldHandleNullCapacityId() {
        // Given
        when(gateway.findByCapacity(null)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(null))
            .verifyComplete();

        verify(gateway).findByCapacity(null);
    }

    @Test
    void shouldHandleZeroCapacityId() {
        // Given
        Long zeroCapacityId = 0L;
        when(gateway.findByCapacity(zeroCapacityId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(zeroCapacityId))
            .verifyComplete();

        verify(gateway).findByCapacity(zeroCapacityId);
    }

    @Test
    void shouldHandleNegativeCapacityId() {
        // Given
        Long negativeCapacityId = -1L;
        when(gateway.findByCapacity(negativeCapacityId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(negativeCapacityId))
            .verifyComplete();

        verify(gateway).findByCapacity(negativeCapacityId);
    }

    private Technology createTechnology(Long id, String name, String description) {
        return new Technology(id, name, description);
    }
}
