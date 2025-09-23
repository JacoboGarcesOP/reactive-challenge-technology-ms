package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.gateway.TechnologyGateway;

public class AssociateTechnologyWithCapacityUseCase {
  private final TechnologyGateway gateway;

  public AssociateTechnologyWithCapacityUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }
}
