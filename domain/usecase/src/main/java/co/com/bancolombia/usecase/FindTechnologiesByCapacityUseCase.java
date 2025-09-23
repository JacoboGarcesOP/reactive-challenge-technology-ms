package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.gateway.TechnologyGateway;

public class FindTechnologiesByCapacityUseCase {
  private final TechnologyGateway gateway;

  public FindTechnologiesByCapacityUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }
}
