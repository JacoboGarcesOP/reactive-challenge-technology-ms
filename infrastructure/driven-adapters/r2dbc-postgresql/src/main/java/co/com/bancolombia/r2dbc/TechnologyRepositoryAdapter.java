package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.TechnologyCapacity;
import co.com.bancolombia.model.technology.gateway.TechnologyGateway;
import co.com.bancolombia.r2dbc.entity.TechnologyCapacityEntity;
import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class TechnologyRepositoryAdapter implements TechnologyGateway {
  private final TechnologyRepository repository;
  private final TechnologyCapacityRepository capacityRepository;

  public TechnologyRepositoryAdapter(TechnologyRepository repository, TechnologyCapacityRepository capacityRepository) {
    this.repository = repository;
    this.capacityRepository = capacityRepository;
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


  @Override
  public Mono<Technology> findByName(String name) {
    return repository.findByName(name)
      .map(entity -> new Technology(entity.getTechnologyId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Flux<Technology> findAll() {
    return repository.findAll()
      .map(entity -> new Technology(entity.getTechnologyId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Flux<Technology> findByCapacity(Long capacityId) {
    return repository.findByCapacity(capacityId)
      .map(entity -> new Technology(entity.getTechnologyId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Mono<TechnologyCapacity> associateTechnologyWithCapacity(TechnologyCapacity technologyCapacity) {
    Long techId = technologyCapacity.getTechnologyId().getValue();
    Long capId = technologyCapacity.getCapacityId().getValue();
    
    TechnologyCapacityEntity entity = TechnologyCapacityEntity.builder()
      .technologyId(techId)
      .capacityId(capId)
      .build();
    
    return capacityRepository.save(entity)
      .map(savedEntity -> new TechnologyCapacity(
        savedEntity.getTechnologyId(),
        savedEntity.getCapacityId()
      ));
  }

  @Override
  public Mono<TechnologyCapacity> findByTechnologyIdAndCapacityId(Long technologyId, Long capacityId) {
    return capacityRepository.findByTechnologyIdAndCapacityId(technologyId, capacityId)
      .map(entity -> new TechnologyCapacity(
        entity.getTechnologyId(),
        entity.getCapacityId()
      ));
  }

  @Override
  @Transactional
  public Mono<Boolean> delete(Long technologyId) {
    return capacityRepository.findAllByTechnologyId(technologyId)
      .flatMap(capacityRepository::delete)
      .then(repository.deleteById(technologyId))
      .thenReturn(Boolean.TRUE);
  }


  @Override
  public Mono<Long> countCapacitiesByTechnologyId(Long technologyId) {
    return capacityRepository.countByTechnologyId(technologyId);
  }

  @Override
  @Transactional
  public Mono<Boolean> deleteTechnologyCapacityRelation(Long technologyId, Long capacityId) {
    return capacityRepository.deleteByTechnologyIdAndCapacityId(technologyId, capacityId)
      .thenReturn(Boolean.TRUE);
  }

}
