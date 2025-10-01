package co.com.bancolombia.model.technology.gateway;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.TechnologyCapacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Technology> findByName(String name);
  Mono<Technology> save(Technology technology);
  Flux<Technology> findAll();
  Flux<Technology> findByCapacity(Long capacityId);
  Mono<TechnologyCapacity> associateTechnologyWithCapacity(TechnologyCapacity technologyCapacity);
  Mono<TechnologyCapacity> findByTechnologyIdAndCapacityId(Long technologyId, Long capacityId);
  Mono<Boolean> delete(Long technologyId);
  Mono<Long> countCapacitiesByTechnologyId(Long technologyId);
  Mono<Boolean> deleteTechnologyCapacityRelation(Long technologyId, Long capacityId);
}
