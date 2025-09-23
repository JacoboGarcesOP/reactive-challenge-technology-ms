package co.com.bancolombia.model.technology.values;

import co.com.bancolombia.model.technology.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameTest {

  @Test
  void shouldCreateNameWithValidValue() {
    // Given
    String validName = "Java";

    // When
    Name name = new Name(validName);

    // Then
    assertNotNull(name);
    assertEquals(validName, name.getValue());
  }

  @Test
  void shouldTrimWhitespaceInName() {
    // Given
    String nameWithWhitespace = "  Java  ";
    String expectedTrimmed = "Java";

    // When
    Name name = new Name(nameWithWhitespace);

    // Then
    assertEquals(expectedTrimmed, name.getValue());
  }

  @Test
  void shouldCreateNameWithMaximumLength() {
    // Given
    String maxLengthName = "a".repeat(50);

    // When
    Name name = new Name(maxLengthName);

    // Then
    assertNotNull(name);
    assertEquals(maxLengthName, name.getValue());
  }

  @Test
  void shouldCreateNameWithSingleCharacter() {
    // Given
    String singleChar = "J";

    // When
    Name name = new Name(singleChar);

    // Then
    assertEquals(singleChar, name.getValue());
  }

  @Test
  void shouldThrowExceptionWhenNameIsNull() {
    // Given
    String nullName = null;

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Name(nullName);
    });

    assertEquals("Technology name cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNameIsEmpty() {
    // Given
    String emptyName = "";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Name(emptyName);
    });

    assertEquals("Technology name cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNameIsOnlyWhitespace() {
    // Given
    String whitespaceOnlyName = "   ";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Name(whitespaceOnlyName);
    });

    assertEquals("Technology name cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNameIsTooLong() {
    // Given
    String tooLongName = "a".repeat(51);

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Name(tooLongName);
    });

    assertEquals("Technology name cannot be greater than 50.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenTrimmedNameIsTooLong() {
    // Given - 51 characters + whitespace
    String tooLongNameWithWhitespace = " " + "a".repeat(51) + " ";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Name(tooLongNameWithWhitespace);
    });

    assertEquals("Technology name cannot be greater than 50.", exception.getMessage());
  }
}