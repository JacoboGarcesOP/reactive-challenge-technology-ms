package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TechnologyRepositoryAdapter implements TechnologyGateway {
  private final TechnologyRepository repository;

  public TechnologyRepositoryAdapter(TechnologyRepository repository) {
    this.repository = repository;
  }

  @Override
  public Mono<Technology> save(Technology model) {
    return repository.save(
      TechnologyEntity
        .builder()
        .description(model.getDescription().getValue())
        .name(model.getName().getValue())
        .build()
    ).map(entity -> new Technology(entity.getTechnologyId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Mono<Boolean> existsByName(String name) {
    return repository.existsByName(name);
  }
}
