package co.com.bancolombia.usecase;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.command.CreateTechnologyCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CreateTechnologyResponse;
import reactor.core.publisher.Mono;

public class CreateTechnologyUseCase {
  private final String SUCCESS_MESSAGE = "Technology created successfuly.";
  private final String TECHNOLOGY_DUPLICATED_MESSAGE = "The technology name cannot be duplicated.";
  private final TechnologyGateway gateway;

  public CreateTechnologyUseCase(TechnologyGateway gateway) {
    this.gateway = gateway;
  }

  public Mono<CreateTechnologyResponse> execute(CreateTechnologyCommand command) {
    return gateway.existsByName(command.getName())
      .flatMap(exists -> {
        if (exists) {
          return Mono.error(new BussinessException(TECHNOLOGY_DUPLICATED_MESSAGE));
        }

        return gateway.save(new Technology(command.getName(), command.getDescription()))
          .map(technology -> new CreateTechnologyResponse(technology.getId().getValue(), technology.getName().getValue(), technology.getDescription().getValue(), SUCCESS_MESSAGE));
      });
  }
}
