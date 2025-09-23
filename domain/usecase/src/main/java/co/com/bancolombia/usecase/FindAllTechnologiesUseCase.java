package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.gateway.TechnologyGateway;

public class FindAllTechnologiesUseCase {
  private final TechnologyGateway gateway;

  public FindAllTechnologiesUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }
}
