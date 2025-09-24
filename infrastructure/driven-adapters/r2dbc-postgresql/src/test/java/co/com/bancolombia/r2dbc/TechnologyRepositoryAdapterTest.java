package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyRepositoryAdapterTest {

  @Mock
  private TechnologyRepository repository;

  @Mock
  private TechnologyCapacityRepository capacityRepository;

  private TechnologyRepositoryAdapter adapter;

  private Technology technology;
  private TechnologyEntity technologyEntity;

  private final Long TECHNOLOGY_ID = 1L;
  private final String TECHNOLOGY_NAME = "Java";
  private final String TECHNOLOGY_DESCRIPTION = "Programming language";

  @BeforeEach
  void setUp() {
    adapter = new TechnologyRepositoryAdapter(repository, capacityRepository);

    technology = new Technology(TECHNOLOGY_ID, TECHNOLOGY_NAME, TECHNOLOGY_DESCRIPTION);

    technologyEntity = TechnologyEntity.builder()
      .technologyId(TECHNOLOGY_ID)
      .name(TECHNOLOGY_NAME)
      .description(TECHNOLOGY_DESCRIPTION)
      .build();
  }


  @Test
  void shouldCreateAdapterWithRepository() {
    // When
    TechnologyRepositoryAdapter newAdapter = new TechnologyRepositoryAdapter(repository, capacityRepository);

    // Then
    assertNotNull(newAdapter);
  }

  @Test
  void shouldSaveTechnologySuccessfully() {
    // Given
    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(technologyEntity));

    // When
    Mono<Technology> result = adapter.save(technology);

    // Then
    StepVerifier.create(result)
      .assertNext(savedTechnology -> {
        assertNotNull(savedTechnology);
        assertEquals(TECHNOLOGY_ID, savedTechnology.getId().getValue());
        assertEquals(TECHNOLOGY_NAME, savedTechnology.getName().getValue());
        assertEquals(TECHNOLOGY_DESCRIPTION, savedTechnology.getDescription().getValue());
      })
      .verifyComplete();

    verify(repository, times(1)).save(any(TechnologyEntity.class));
  }

  @Test
  void shouldMapModelToEntityCorrectlyWhenSaving() {
    // Given
    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(technologyEntity));

    // When
    adapter.save(technology).block();

    // Then
    verify(repository).save(argThat(entity ->
      entity.getName().equals(TECHNOLOGY_NAME) &&
        entity.getDescription().equals(TECHNOLOGY_DESCRIPTION) &&
        entity.getTechnologyId() == null // ID should be null for new entities
    ));
  }

  @Test
  void shouldHandleRepositoryErrorWhenSaving() {
    // Given
    RuntimeException repositoryException = new RuntimeException("Database error");
    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.error(repositoryException));

    // When
    Mono<Technology> result = adapter.save(technology);

    // Then
    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(repository, times(1)).save(any(TechnologyEntity.class));
  }

  @Test
  void shouldSaveWithDifferentTechnologyValues() {
    // Given
    Technology differentTechnology = new Technology(2L, "Python", "Scripting language");
    TechnologyEntity differentEntity = TechnologyEntity.builder()
      .technologyId(2L)
      .name("Python")
      .description("Scripting language")
      .build();

    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(differentEntity));

    // When
    Mono<Technology> result = adapter.save(differentTechnology);

    // Then
    StepVerifier.create(result)
      .assertNext(savedTechnology -> {
        assertEquals(2L, savedTechnology.getId().getValue());
        assertEquals("Python", savedTechnology.getName().getValue());
        assertEquals("Scripting language", savedTechnology.getDescription().getValue());
      })
      .verifyComplete();
  }

  @Test
  void shouldReturnTrueWhenTechnologyExistsByName() {
    // Given
    String existingName = "Java";
    when(repository.existsByName(existingName)).thenReturn(Mono.just(true));

    // When
    Mono<Boolean> result = adapter.existsByName(existingName);

    // Then
    StepVerifier.create(result)
      .expectNext(true)
      .verifyComplete();

    verify(repository, times(1)).existsByName(existingName);
  }

  @Test
  void shouldReturnFalseWhenTechnologyDoesNotExistByName() {
    // Given
    String nonExistingName = "NonExisting";
    when(repository.existsByName(nonExistingName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = adapter.existsByName(nonExistingName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(repository, times(1)).existsByName(nonExistingName);
  }

  @Test
  void shouldHandleNullNameInExistsByName() {
    // Given
    String nullName = null;
    when(repository.existsByName(nullName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = adapter.existsByName(nullName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(repository, times(1)).existsByName(nullName);
  }

  @Test
  void shouldHandleEmptyNameInExistsByName() {
    // Given
    String emptyName = "";
    when(repository.existsByName(emptyName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = adapter.existsByName(emptyName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(repository, times(1)).existsByName(emptyName);
  }

  @Test
  void shouldHandleRepositoryErrorInExistsByName() {
    // Given
    String name = "Java";
    RuntimeException repositoryException = new RuntimeException("Database connection error");
    when(repository.existsByName(name)).thenReturn(Mono.error(repositoryException));

    // When
    Mono<Boolean> result = adapter.existsByName(name);

    // Then
    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(repository, times(1)).existsByName(name);
  }

  @Test
  void shouldHandleCaseInsensitiveNameSearch() {
    // Given
    String upperCaseName = "JAVA";
    when(repository.existsByName(upperCaseName)).thenReturn(Mono.just(true));

    // When
    Mono<Boolean> result = adapter.existsByName(upperCaseName);

    // Then
    StepVerifier.create(result)
      .expectNext(true)
      .verifyComplete();

    verify(repository, times(1)).existsByName(upperCaseName);
  }

  @Test
  void shouldVerifyRepositoryIsCalledWithCorrectParameters() {
    // Given
    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(technologyEntity));

    // When
    adapter.save(technology).block();

    // Then
    verify(repository).save(argThat(entity -> {
      assertNotNull(entity);
      assertEquals(TECHNOLOGY_NAME, entity.getName());
      assertEquals(TECHNOLOGY_DESCRIPTION, entity.getDescription());
      return true;
    }));
  }

  @Test
  void shouldHandleEmptyMonoFromRepository() {
    // Given
    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.empty());

    // When
    Mono<Technology> result = adapter.save(technology);

    // Then
    StepVerifier.create(result)
      .verifyComplete();

    verify(repository, times(1)).save(any(TechnologyEntity.class));
  }

  @Test
  void shouldWorkWithLongTechnologyNames() {
    // Given
    String longName = "a".repeat(50); // Maximum length allowed
    String longDescription = "b".repeat(90); // Maximum length allowed

    Technology longTechnology = new Technology(1L, longName, longDescription);
    TechnologyEntity longEntity = TechnologyEntity.builder()
      .technologyId(1L)
      .name(longName)
      .description(longDescription)
      .build();

    when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(longEntity));

    // When
    Mono<Technology> result = adapter.save(longTechnology);

    // Then
    StepVerifier.create(result)
      .assertNext(savedTechnology -> {
        assertEquals(longName, savedTechnology.getName().getValue());
        assertEquals(longDescription, savedTechnology.getDescription().getValue());
      })
      .verifyComplete();
  }
}