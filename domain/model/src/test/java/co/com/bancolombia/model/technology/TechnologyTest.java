package co.com.bancolombia.model.technology;

import co.com.bancolombia.model.technology.values.Description;
import co.com.bancolombia.model.technology.values.Id;
import co.com.bancolombia.model.technology.values.Name;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TechnologyTest {

    private Technology technology;
    private final Long validId = 1L;
    private final String validName = "Java";
    private final String validDescription = "Programming language";

    @BeforeEach
    void setUp() {
      technology = new Technology(validId, validName, validDescription);
    }

    @Test
    void shouldCreateTechnologyWithValidValues() {
      // Given & When
      Technology tech = new Technology(validId, validName, validDescription);

      // Then
      assertNotNull(tech);
      assertEquals(validId, tech.getId().getValue());
      assertEquals(validName, tech.getName().getValue());
      assertEquals(validDescription, tech.getDescription().getValue());
    }

    @Test
    void shouldGetId() {
      // When
      Id result = technology.getId();

      // Then
      assertNotNull(result);
      assertEquals(validId, result.getValue());
    }

    @Test
    void shouldSetId() {
      // Given
      Id newId = new Id(2L);

      // When
      technology.setId(newId);

      // Then
      assertEquals(newId, technology.getId());
      assertEquals(2L, technology.getId().getValue());
    }

    @Test
    void shouldGetName() {
      // When
      Name result = technology.getName();

      // Then
      assertNotNull(result);
      assertEquals(validName, result.getValue());
    }

    @Test
    void shouldSetName() {
      // Given
      Name newName = new Name("Python");

      // When
      technology.setName(newName);

      // Then
      assertEquals(newName, technology.getName());
      assertEquals("Python", technology.getName().getValue());
    }

    @Test
    void shouldGetDescription() {
      // When
      Description result = technology.getDescription();

      // Then
      assertNotNull(result);
      assertEquals(validDescription, result.getValue());
    }

    @Test
    void shouldSetDescription() {
      // Given
      Description newDescription = new Description("Object-oriented programming language");

      // When
      technology.setDescription(newDescription);

      // Then
      assertEquals(newDescription, technology.getDescription());
      assertEquals("Object-oriented programming language", technology.getDescription().getValue());
    }
}