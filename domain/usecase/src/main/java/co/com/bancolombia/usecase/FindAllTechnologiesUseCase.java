package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Flux;

public class FindAllTechnologiesUseCase {
  private final TechnologyGateway gateway;

  public FindAllTechnologiesUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }

  public Flux<TechnologyResponse> execute() {
    return gateway.findAll().map(technology -> new TechnologyResponse(technology.getId().getValue(), technology.getName().getValue(), technology.getDescription().getValue()));
  }
}
