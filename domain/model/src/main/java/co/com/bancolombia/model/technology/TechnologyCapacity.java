package co.com.bancolombia.model.technology;

import co.com.bancolombia.model.technology.values.Id;

public class TechnologyCapacity {
  private Id technologyId;
  private Id capacityId;

  public TechnologyCapacity(Long technologyId, Long capacityId) {
    this.technologyId = new Id(technologyId);
    this.capacityId = new Id(capacityId);
  }

  public Id getTechnologyId() {
    return technologyId;
  }

  public void setTechnologyId(Id technologyId) {
    this.technologyId = technologyId;
  }

  public Id getCapacityId() {
    return capacityId;
  }

  public void setCapacityId(Id capacityId) {
    this.capacityId = capacityId;
  }
}
