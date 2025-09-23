package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateTechnologyRequest;
import co.com.bancolombia.api.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
  private final String BASE_URL = "/v1/api";

  @Bean
  @RouterOperation(
    path = "/v1/api/technology",
    method = RequestMethod.POST,
    operation = @Operation(
      operationId = "createTechnology",
      summary = "Crear nueva tecnología",
      description = "Endpoint para registrar una nueva tecnología en el sistema. " +
        "Valida los datos de entrada y maneja diferentes tipos de errores " +
        "como validaciones, errores de dominio, errores de negocio e errores internos.",
      tags = {"Technology Management"},
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos de la tecnología a crear. Requiere nombre (máx 50 caracteres) y descripción (máx 90 caracteres).",
        required = true,
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = CreateTechnologyRequest.class),
          examples = @ExampleObject(
            name = "Ejemplo de tecnología",
            summary = "Ejemplo de request para crear una tecnología",
            value = "{\n" +
              "  \"name\": \"Spring Boot\",\n" +
              "  \"description\": \"Framework de Java para desarrollo de aplicaciones empresariales\"\n" +
              "}"
          )
        )
      ),
      responses = {
        @ApiResponse(
          responseCode = "200",
          description = "Tecnología creada exitosamente",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(
              description = "Respuesta exitosa al crear una nueva tecnología"
            ),
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Tecnología creada correctamente",
              value = "{\n" +
                "  \"technologyId\": 123,\n" +
                "  \"name\": \"Spring Boot\",\n" +
                "  \"description\": \"Framework de Java para desarrollo de aplicaciones empresariales\",\n" +
                "  \"message\": \"Technology created successfully\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(
          responseCode = "400",
          description = "Error de validación, dominio o negocio",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                name = "Validation Error",
                summary = "Error de validación de campos",
                value = "{\n" +
                  "  \"error\": \"VALIDATION_ERROR\",\n" +
                  "  \"message\": \"Technology name is required, Technology description cannot be empty\"\n" +
                  "}"
              ),
              @ExampleObject(
                name = "Domain Error",
                summary = "Error de reglas de dominio",
                value = "{\n" +
                  "  \"error\": \"DOMAIN_ERROR\",\n" +
                  "  \"message\": \"Technology already exists in the system\"\n" +
                  "}"
              ),
              @ExampleObject(
                name = "Business Error",
                summary = "Error de reglas de negocio",
                value = "{\n" +
                  "  \"error\": \"BUSINESS_ERROR\",\n" +
                  "  \"message\": \"Cannot create technology at this time\"\n" +
                  "}"
              )
            }
          )
        ),
        @ApiResponse(
          responseCode = "500",
          description = "Error interno del servidor",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno del sistema",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> routerFunction(Handler handler) {
    return route(POST(BASE_URL + "/technology"), handler::createTechnology);
  }
}