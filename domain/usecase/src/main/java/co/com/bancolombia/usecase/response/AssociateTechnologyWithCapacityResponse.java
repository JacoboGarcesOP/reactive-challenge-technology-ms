package co.com.bancolombia.usecase.response;

public class AssociateTechnologyWithCapacityResponse extends TechnologyResponse {
  private final Long capacityId;

  public AssociateTechnologyWithCapacityResponse(Long technologyId, String name, String description, Long capacityId) {
    super(technologyId, name, description);
    this.capacityId = capacityId;
  }

  public Long getCapacityId() {
    return capacityId;
  }
}
