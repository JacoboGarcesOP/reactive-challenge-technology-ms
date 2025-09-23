package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "technology", schema = "tech_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder()
public class TechnologyEntity {
  @Id
  @Column("technology_id")
  private Long technologyId;
  private String name;
  private String description;
}
