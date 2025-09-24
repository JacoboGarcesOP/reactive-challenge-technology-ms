package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Flux;

public class FindTechnologiesByCapacityUseCase {
  private final TechnologyGateway gateway;

  public FindTechnologiesByCapacityUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }

  public Flux<TechnologyResponse> execute(Long capacityId) {
    return gateway
      .findByCapacity(capacityId)
      .map(technology -> new TechnologyResponse(technology.getId().getValue(), technology.getName().getValue(), technology.getDescription().getValue()));
  }
}
