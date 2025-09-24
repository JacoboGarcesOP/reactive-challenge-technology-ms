package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.TechnologyCapacity;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.command.AssociateTechnologyWithCapacityCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.AssociateTechnologyWithCapacityResponse;
import reactor.core.publisher.Mono;

public class AssociateTechnologyWithCapacityUseCase {
  private final String TECHNOLOGY_NOT_FOUND_MESSAGE = "The technology name has not been found.";
  private final String ASSOCIATION_ALREADY_EXISTS_MESSAGE = "The technology is already associated with this capacity.";
  private final TechnologyGateway gateway;

  public AssociateTechnologyWithCapacityUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }

  public Mono<AssociateTechnologyWithCapacityResponse> execute(AssociateTechnologyWithCapacityCommand command) {
    return gateway.findByName(command.getTechnology())
      .flatMap(technology -> {
        if (technology == null) {
          return Mono.error(new BussinessException(TECHNOLOGY_NOT_FOUND_MESSAGE));
        }

        Long technologyId = technology.getId().getValue();
        Long capacityId = command.getCapacityId();

        return gateway.findByTechnologyIdAndCapacityId(technologyId, capacityId)
          .flatMap(existingAssociation -> 
            Mono.error(new BussinessException(ASSOCIATION_ALREADY_EXISTS_MESSAGE))
          )
          .cast(AssociateTechnologyWithCapacityResponse.class)
          .switchIfEmpty(
            gateway.associateTechnologyWithCapacity(new TechnologyCapacity(technologyId, capacityId))
              .map(capacity -> new AssociateTechnologyWithCapacityResponse(technology.getId().getValue(), technology.getName().getValue(), technology.getDescription().getValue(), capacity.getCapacityId().getValue()))
          );
      });
  }
}
