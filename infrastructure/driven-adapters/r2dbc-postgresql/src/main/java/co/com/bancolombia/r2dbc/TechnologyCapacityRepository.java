package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.TechnologyCapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TechnologyCapacityRepository extends ReactiveCrudRepository<TechnologyCapacityEntity, Long> {
  Mono<TechnologyCapacityEntity> findByTechnologyIdAndCapacityId(Long technologyId, Long capacityId);
}
