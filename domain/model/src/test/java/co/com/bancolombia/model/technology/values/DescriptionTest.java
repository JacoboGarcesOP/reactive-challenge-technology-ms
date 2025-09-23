package co.com.bancolombia.model.technology.values;

import co.com.bancolombia.model.technology.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DescriptionTest {

  @Test
  void shouldCreateDescriptionWithValidValue() {
    // Given
    String validDescription = "Programming language for enterprise applications";

    // When
    Description description = new Description(validDescription);

    // Then
    assertNotNull(description);
    assertEquals(validDescription, description.getValue());
  }

  @Test
  void shouldTrimWhitespaceInDescription() {
    // Given
    String descriptionWithWhitespace = "  Programming language  ";
    String expectedTrimmed = "Programming language";

    // When
    Description description = new Description(descriptionWithWhitespace);

    // Then
    assertEquals(expectedTrimmed, description.getValue());
  }

  @Test
  void shouldCreateDescriptionWithMaximumLength() {
    // Given
    String maxLengthDescription = "a".repeat(90);

    // When
    Description description = new Description(maxLengthDescription);

    // Then
    assertNotNull(description);
    assertEquals(maxLengthDescription, description.getValue());
  }

  @Test
  void shouldCreateDescriptionWithSingleCharacter() {
    // Given
    String singleChar = "D";

    // When
    Description description = new Description(singleChar);

    // Then
    assertEquals(singleChar, description.getValue());
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsNull() {
    // Given
    String nullDescription = null;

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Description(nullDescription);
    });

    assertEquals("Technology description cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsEmpty() {
    // Given
    String emptyDescription = "";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Description(emptyDescription);
    });

    assertEquals("Technology description cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsOnlyWhitespace() {
    // Given
    String whitespaceOnlyDescription = "   ";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Description(whitespaceOnlyDescription);
    });

    assertEquals("Technology description cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsTooLong() {
    // Given
    String tooLongDescription = "a".repeat(91);

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Description(tooLongDescription);
    });

    assertEquals("Technology description cannot be greater than 90.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenTrimmedDescriptionIsTooLong() {
    // Given - 91 characters + whitespace
    String tooLongDescriptionWithWhitespace = " " + "a".repeat(91) + " ";

    // When & Then
    DomainException exception = assertThrows(DomainException.class, () -> {
      new Description(tooLongDescriptionWithWhitespace);
    });

    assertEquals("Technology description cannot be greater than 90.", exception.getMessage());
  }
}