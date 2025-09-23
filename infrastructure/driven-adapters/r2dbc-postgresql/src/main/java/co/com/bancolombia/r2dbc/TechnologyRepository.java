package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {
  Mono<Boolean> existsByName(String name);
}
