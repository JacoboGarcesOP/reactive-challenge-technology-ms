package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.model.technology.values.Description;
import co.com.bancolombia.model.technology.values.Id;
import co.com.bancolombia.model.technology.values.Name;
import co.com.bancolombia.usecase.command.CreateTechnologyCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CreateTechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTechnologyUseCaseTest {

    private static final String TECHNOLOGY_NAME = "Java";
    private static final String TECHNOLOGY_DESCRIPTION = "Programming language";
    private static final Long TECHNOLOGY_ID = 1L;
    private static final String SUCCESS_MESSAGE = "Technology created successfuly.";
    private static final String TECHNOLOGY_DUPLICATED_MESSAGE = "The technology name cannot be duplicated.";

    @Mock
    private TechnologyGateway gateway;

    private CreateTechnologyUseCase useCase;
    private CreateTechnologyCommand command;

    @BeforeEach
    void setUp() {
        useCase = new CreateTechnologyUseCase(gateway);
        command = new CreateTechnologyCommand(TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
    }

    @Test
    void shouldCreateTechnologySuccessfully_whenTechnologyDoesNotExist() {
        // Given
        Technology savedTechnology = createTechnologyWithId(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        
        when(gateway.existsByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(false));
        when(gateway.save(any(Technology.class))).thenReturn(Mono.just(savedTechnology));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .assertNext(response -> {
                assertThat(response.getTechnologyId()).isEqualTo(TECHNOLOGY_ID);
                assertThat(response.getName()).isEqualTo(TECHNOLOGY_NAME);
                assertThat(response.getDescription()).isEqualTo(TECHNOLOGY_DESCRIPTION);
                assertThat(response.getMessage()).isEqualTo(SUCCESS_MESSAGE);
            })
            .verifyComplete();

        verify(gateway).existsByName(TECHNOLOGY_NAME);
        verify(gateway).save(any(Technology.class));
    }

    @Test
    void shouldThrowBussinessException_whenTechnologyAlreadyExists() {
        // Given
        when(gateway.existsByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals(TECHNOLOGY_DUPLICATED_MESSAGE))
            .verify();

        verify(gateway).existsByName(TECHNOLOGY_NAME);
    }

    @Test
    void shouldPropagateError_whenGatewayExistsByNameFails() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(gateway.existsByName(TECHNOLOGY_NAME)).thenReturn(Mono.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).existsByName(TECHNOLOGY_NAME);
    }

    @Test
    void shouldPropagateError_whenGatewaySaveFails() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database save error");
        when(gateway.existsByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(false));
        when(gateway.save(any(Technology.class))).thenReturn(Mono.error(gatewayException));

        // When & Then
        StepVerifier.create(useCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(gateway).existsByName(TECHNOLOGY_NAME);
        verify(gateway).save(any(Technology.class));
    }

    @Test
    void shouldCallGatewayWithCorrectTechnology_whenCreatingTechnology() {
        // Given
        Technology savedTechnology = createTechnologyWithId(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);
        
        when(gateway.existsByName(TECHNOLOGY_NAME)).thenReturn(Mono.just(false));
        when(gateway.save(any(Technology.class))).thenReturn(Mono.just(savedTechnology));

        // When
        StepVerifier.create(useCase.execute(command))
            .expectNextCount(1)
            .verifyComplete();

        // Then
        verify(gateway).save(any(Technology.class));
    }

    private Technology createTechnologyWithId(Long id, String name, String description) {
        return new Technology(id, name, description);
    }
}