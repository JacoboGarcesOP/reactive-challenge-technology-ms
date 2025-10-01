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
class FindAllTechnologiesUseCaseTest {

    private static final Long TECHNOLOGY_ID_1 = 1L;
    private static final String TECHNOLOGY_NAME_1 = "Java";
    private static final String TECHNOLOGY_DESCRIPTION_1 = "Programming language";
    
    private static final Long TECHNOLOGY_ID_2 = 2L;
    private static final String TECHNOLOGY_NAME_2 = "Spring Boot";
    private static final String TECHNOLOGY_DESCRIPTION_2 = "Java framework";

    @Mock
    private TechnologyGateway gateway;

    private FindAllTechnologiesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindAllTechnologiesUseCase(gateway);
    }

    @Test
    void shouldFindAllTechnologiesSuccessfully_whenTechnologiesExist() {
        // Given
        Technology technology1 = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        Technology technology2 = createTechnology(TECHNOLOGY_ID_2, TECHNOLOGY_NAME_2, TECHNOLOGY_DESCRIPTION_2);
        List<Technology> technologies = List.of(technology1, technology2);

        when(gateway.findAll()).thenReturn(Flux.fromIterable(technologies));

        // When & Then
        StepVerifier.create(useCase.execute())
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

        verify(gateway).findAll();
    }

    @Test
    void shouldReturnEmptyFlux_whenNoTechnologiesExist() {
        // Given
        when(gateway.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute())
            .verifyComplete();

        verify(gateway).findAll();
    }

    @Test
    void shouldReturnSingleTechnology_whenOnlyOneTechnologyExists() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        when(gateway.findAll()).thenReturn(Flux.just(technology));

        // When & Then
        StepVerifier.create(useCase.execute())
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID_1);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME_1);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION_1);
            })
            .verifyComplete();

        verify(gateway).findAll();
    }

    @Test
    void shouldPropagateError_whenGatewayFindAllFails() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(gateway.findAll()).thenReturn(Flux.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute())
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).findAll();
    }

    @Test
    void shouldMapTechnologyToResponseCorrectly() {
        // Given
        Technology technology = createTechnology(TECHNOLOGY_ID_1, TECHNOLOGY_NAME_1, TECHNOLOGY_DESCRIPTION_1);
        when(gateway.findAll()).thenReturn(Flux.just(technology));

        // When
        StepVerifier.create(useCase.execute())
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
    void shouldHandleMultipleTechnologiesCorrectly() {
        // Given
        Technology technology1 = createTechnology(1L, "Java", "Programming language");
        Technology technology2 = createTechnology(2L, "Python", "Scripting language");
        Technology technology3 = createTechnology(3L, "JavaScript", "Web language");
        
        when(gateway.findAll()).thenReturn(Flux.just(technology1, technology2, technology3));

        // When & Then
        StepVerifier.create(useCase.execute())
            .assertNext(response -> assertThat(response.getName()).isEqualTo("Java"))
            .assertNext(response -> assertThat(response.getName()).isEqualTo("Python"))
            .assertNext(response -> assertThat(response.getName()).isEqualTo("JavaScript"))
            .verifyComplete();

        verify(gateway).findAll();
    }

    private Technology createTechnology(Long id, String name, String description) {
        return new Technology(id, name, description);
    }
}
