package co.com.bancolombia.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta estándar para errores de la API")
public class ErrorResponse {

  @Schema(
    description = "Tipo de error",
    example = "VALIDATION_ERROR",
    allowableValues = {"VALIDATION_ERROR", "DOMAIN_ERROR", "BUSINESS_ERROR", "INTERNAL_ERROR"}
  )
  private String error;

  @Schema(
    description = "Mensaje descriptivo del error",
    example = "Technology name is required"
  )
  private String message;
}