package co.com.bancolombia.model.technology.gateway;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.TechnologyCapacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Technology> save(Technology technology);
  Flux<Technology> findAll();
  Flux<Technology> findByCapacity(Long capacityId);
  Mono<TechnologyCapacity> associateTechnologyWithCapacity(TechnologyCapacity technologyCapacity);
}
