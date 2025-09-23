package co.com.bancolombia.model.technology.gateway;

import co.com.bancolombia.model.technology.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Technology> save(Technology technology);
}
