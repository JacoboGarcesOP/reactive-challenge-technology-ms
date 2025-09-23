package co.com.bancolombia.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Request para crear una nueva tecnología en el sistema")
public class CreateTechnologyRequest {

  @NotNull(message = "Technology name is required")
  @NotBlank(message = "Technology name cannot be empty")
  @Size(max = 50, message = "Technology name cannot be greater than 50 characters")
  @Schema(
    description = "Nombre de la tecnología",
    example = "Spring Boot",
    requiredMode = Schema.RequiredMode.REQUIRED,
    maxLength = 50
  )
  private String name;

  @NotNull(message = "Technology description is required")
  @NotBlank(message = "Technology description cannot be empty")
  @Size(max = 90, message = "Technology description cannot be greater than 90 characters")
  @Schema(
    description = "Descripción detallada de la tecnología",
    example = "Framework de Java para desarrollo de aplicaciones empresariales",
    requiredMode = Schema.RequiredMode.REQUIRED,
    maxLength = 90
  )
  private String description;
}