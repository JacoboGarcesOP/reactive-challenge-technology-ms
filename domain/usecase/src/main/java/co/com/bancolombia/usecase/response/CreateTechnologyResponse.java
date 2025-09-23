package co.com.bancolombia.usecase.response;

public class CreateTechnologyResponse {
  private final Long technologyId;
  private final String name;
  private final String description;

  public CreateTechnologyResponse(Long technologyId, String name, String description) {
    this.technologyId = technologyId;
    this.name = name;
    this.description = description;
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
}
