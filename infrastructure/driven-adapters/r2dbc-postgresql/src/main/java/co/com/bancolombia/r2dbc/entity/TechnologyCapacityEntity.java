package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "technology_capacity", schema = "tech_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechnologyCapacityEntity {
  @Id
  private Long id;
  
  @Column("technology_id")
  private Long technologyId;
  
  @Column("capacity_id")
  private Long capacityId;
}
