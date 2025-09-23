package co.com.bancolombia.usecase.response;

public class CreateTechnologyResponse {
  private final Long technologyId;
  private final String name;
  private final String description;
  private final String message;

  public CreateTechnologyResponse(Long technologyId, String name, String description, String message) {
    this.technologyId = technologyId;
    this.name = name;
    this.description = description;
    this.message = message;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Long getTechnologyId() {
    return technologyId;
  }

  public String getMessage() {
    return message;
  }
}
