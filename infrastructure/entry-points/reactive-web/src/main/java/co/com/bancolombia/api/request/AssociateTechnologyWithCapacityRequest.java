package co.com.bancolombia.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Request para asociar una tecnología existente con una capacidad específica")
public class AssociateTechnologyWithCapacityRequest {
  
  @NotNull(message = "Capacity id is required")
  @Positive(message = "The capacity id should be positive")
  @Schema(
    description = "ID de la capacidad a la cual se asociará la tecnología",
    example = "1",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minimum = "1"
  )
  private Long capacityId;

  @NotNull(message = "Technology name is required")
  @NotBlank(message = "Technology name cannot be empty")
  @Size(max = 50, message = "Technology name cannot be greater than 50 characters")
  @Schema(
    description = "Nombre de la tecnología a asociar con la capacidad",
    example = "Spring Boot",
    requiredMode = Schema.RequiredMode.REQUIRED,
    maxLength = 50
  )
  private String technology;
}
