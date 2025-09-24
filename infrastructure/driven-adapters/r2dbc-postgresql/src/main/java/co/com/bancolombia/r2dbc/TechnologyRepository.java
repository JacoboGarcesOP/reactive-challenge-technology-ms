package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {
  Mono<Boolean> existsByName(String name);
  
  Mono<TechnologyEntity> findByName(String name);
  
  @Query("SELECT t.* FROM tech_schema.technology t " +
         "JOIN tech_schema.technology_capacity tc ON t.technology_id = tc.technology_id " +
         "WHERE tc.capacity_id = :capacityId")
  Flux<TechnologyEntity> findByCapacity(Long capacityId);
}
