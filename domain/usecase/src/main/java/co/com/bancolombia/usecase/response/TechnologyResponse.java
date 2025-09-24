package co.com.bancolombia.usecase.response;

public class TechnologyResponse {
  private final Long technologyId;
  private final String name;
  private final String description;

  public TechnologyResponse(Long technologyId, String name, String description) {
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
