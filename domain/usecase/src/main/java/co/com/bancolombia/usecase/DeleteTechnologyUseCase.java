package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class DeleteTechnologyUseCase {
  private final String CAPACITY_ID_CANNOT_BE_NULL_MESSAGE = "Capacity ID cannot be null";
  private final String CAPACITY_NOT_FOUND_MESSAGE = "Capacity has not been found. Capacity id: ";
  private final TechnologyGateway gateway;

  public DeleteTechnologyUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }

  public Mono<List<Long>> execute(Long capacityId) {
    if (capacityId == null) {
      return Mono.error(new BussinessException(CAPACITY_ID_CANNOT_BE_NULL_MESSAGE));
    }

    return gateway.findByCapacity(capacityId)
      .collectList()
      .flatMap(technologies -> {
        if (technologies.isEmpty()) {
          return Mono.error(new BussinessException(CAPACITY_NOT_FOUND_MESSAGE + capacityId));
        }

        return Flux.fromIterable(technologies)
          .flatMap(technology -> {
            Long technologyId = technology.getId().getValue();
            
            return gateway.countCapacitiesByTechnologyId(technologyId)
              .flatMap(capacityCount -> {
                if (capacityCount == 1) {
                  return gateway.delete(technologyId)
                    .then(Mono.just(technologyId));
                } else {
                  return gateway.deleteTechnologyCapacityRelation(technologyId, capacityId)
                    .then(Mono.just(technologyId));
                }
              });
          })
          .collectList();
      });
  }
}
