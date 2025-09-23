package co.com.bancolombia.model.technology.values;

import co.com.bancolombia.model.technology.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    void shouldCreateIdWithValidValue() {
        // Given
        Long validValue = 1L;

        // When
        Id id = new Id(validValue);

        // Then
        assertNotNull(id);
        assertEquals(validValue, id.getValue());
    }

    @Test
    void shouldCreateIdWithZeroValue() {
        // Given
        Long zeroValue = 0L;

        // When
        Id id = new Id(zeroValue);

        // Then
        assertNotNull(id);
        assertEquals(zeroValue, id.getValue());
    }

    @Test
    void shouldCreateIdWithNegativeValue() {
        // Given
        Long negativeValue = -1L;

        // When
        Id id = new Id(negativeValue);

        // Then
        assertNotNull(id);
        assertEquals(negativeValue, id.getValue());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Given
        Long nullValue = null;

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Id(nullValue);
        });

        assertEquals("Technology id cannot be null.", exception.getMessage());
    }
}