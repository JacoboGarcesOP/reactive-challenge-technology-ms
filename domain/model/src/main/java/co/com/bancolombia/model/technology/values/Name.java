package co.com.bancolombia.model.technology.values;

import co.com.bancolombia.model.technology.exceptions.DomainException;

public class Name {

  private static final String NULL_NAME_ERROR_MESSAGE = "Technology name cannot be null.";
  private static final String MAX_LENGTH_ERROR_MESSAGE = "Technology name cannot be greater than 50.";
  private static final int MAX_NAME_LENGTH = 50;

  private final String value;

  public Name(final String value) {

    if (value == null || value.trim().isEmpty()) {
      throw new DomainException(NULL_NAME_ERROR_MESSAGE);
    }

    if (value.trim().length() > MAX_NAME_LENGTH) {
      throw new DomainException(MAX_LENGTH_ERROR_MESSAGE);
    }

    this.value = value.trim();
  }

  public String getValue() {
    return value;
  }
}