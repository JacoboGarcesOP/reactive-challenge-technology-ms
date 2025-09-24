package co.com.bancolombia.usecase.command;

public class AssociateTechnologyWithCapacityCommand {
  private final Long capacityId;
  private final String technology;

  public AssociateTechnologyWithCapacityCommand(Long capacityId, String technology) {
    this.capacityId = capacityId;
    this.technology = technology;
  }

  public Long getCapacityId() {
    return capacityId;
  }

  public String getTechnology() {
    return technology;
  }
}
