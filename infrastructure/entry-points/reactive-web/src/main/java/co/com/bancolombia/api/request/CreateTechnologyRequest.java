package co.com.bancolombia.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTechnologyRequest {

  @NotNull(message = "Technology name is required")
  @NotBlank(message = "Technology name cannot be empty")
  @Size(max = 50, message = "Technology name cannot be greater than 50 characters")
  private String name;

  @NotNull(message = "Technology description is required")
  @NotBlank(message = "Technology description cannot be empty")
  @Size(max = 90, message = "Technology description cannot be greater than 90 characters")
  private String description;
}
