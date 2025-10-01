package co.com.bancolombia.api;

import co.com.bancolombia.api.request.AssociateTechnologyWithCapacityRequest;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
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
  public RouterFunction<ServerResponse> createTechnologyRouter(Handler handler) {
    return route(POST(BASE_URL + "/technology"), handler::createTechnology);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/technology",
    method = RequestMethod.GET,
    operation = @Operation(
      operationId = "findAllTechnologies",
      summary = "Obtener todas las tecnologías",
      description = "Endpoint para obtener la lista completa de tecnologías disponibles en el sistema. " +
        "No requiere parámetros de entrada y retorna todas las tecnologías registradas.",
      tags = {"Technology Management"},
      responses = {
        @ApiResponse(
          responseCode = "200",
          description = "Lista de tecnologías obtenida exitosamente",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(
              description = "Lista de tecnologías disponibles"
            ),
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de tecnologías",
              value = "[\n" +
                "  {\n" +
                "    \"technologyId\": 1,\n" +
                "    \"name\": \"Spring Boot\",\n" +
                "    \"description\": \"Framework de Java para desarrollo de aplicaciones empresariales\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"technologyId\": 2,\n" +
                "    \"name\": \"React\",\n" +
                "    \"description\": \"Biblioteca de JavaScript para construir interfaces de usuario\"\n" +
                "  }\n" +
                "]"
            )
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
  public RouterFunction<ServerResponse> findAllTechnologiesRouter(Handler handler) {
    return route(GET(BASE_URL + "/technology"), handler::findAllTechnologies);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/technology/capacity/{capacityId}",
    method = RequestMethod.GET,
    operation = @Operation(
      operationId = "findTechnologiesByCapacity",
      summary = "Obtener tecnologías por capacidad",
      description = "Endpoint para obtener las tecnologías asociadas a una capacidad específica. " +
        "Requiere el ID de la capacidad como parámetro de ruta.",
      tags = {"Technology Management"},
      responses = {
        @ApiResponse(
          responseCode = "200",
          description = "Lista de tecnologías por capacidad obtenida exitosamente",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(
              description = "Lista de tecnologías asociadas a la capacidad especificada"
            ),
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Tecnologías por capacidad",
              value = "[\n" +
                "  {\n" +
                "    \"technologyId\": 1,\n" +
                "    \"name\": \"Spring Boot\",\n" +
                "    \"description\": \"Framework de Java para desarrollo de aplicaciones empresariales\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"technologyId\": 3,\n" +
                "    \"name\": \"Docker\",\n" +
                "    \"description\": \"Plataforma de contenedores para desarrollo y despliegue\"\n" +
                "  }\n" +
                "]"
            )
          )
        ),
        @ApiResponse(
          responseCode = "400",
          description = "Error de validación o negocio",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de reglas de negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"Capacity not found\"\n" +
                "}"
            )
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
  public RouterFunction<ServerResponse> findTechnologiesByCapacityRouter(Handler handler) {
    return route(GET(BASE_URL + "/technology/capacity/{capacityId}"), handler::findTechnologiesByCapacity);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/technology/associate",
    method = RequestMethod.POST,
    operation = @Operation(
      operationId = "associateTechnologyWithCapacity",
      summary = "Asociar tecnología con capacidad",
      description = "Endpoint para asociar una tecnología existente con una capacidad específica. " +
        "Requiere el ID de la capacidad y el nombre de la tecnología en el cuerpo de la petición.",
      tags = {"Technology Management"},
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos para asociar tecnología con capacidad. Requiere capacityId (positivo) y technology (máx 50 caracteres).",
        required = true,
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AssociateTechnologyWithCapacityRequest.class),
          examples = @ExampleObject(
            name = "Ejemplo de asociación",
            summary = "Ejemplo de request para asociar tecnología con capacidad",
            value = "{\n" +
              "  \"capacityId\": 1,\n" +
              "  \"technology\": \"Spring Boot\"\n" +
              "}"
          )
        )
      ),
      responses = {
        @ApiResponse(
          responseCode = "200",
          description = "Tecnología asociada exitosamente",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(
              description = "Respuesta exitosa al asociar tecnología con capacidad"
            ),
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Tecnología asociada correctamente",
              value = "{\n" +
                "  \"technologyId\": 1,\n" +
                "  \"name\": \"Spring Boot\",\n" +
                "  \"description\": \"Framework de Java para desarrollo de aplicaciones empresariales\",\n" +
                "  \"capacityId\": 1\n" +
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
                  "  \"message\": \"Capacity id is required, Technology name cannot be empty\"\n" +
                  "}"
              ),
              @ExampleObject(
                name = "Business Error",
                summary = "Error de reglas de negocio",
                value = "{\n" +
                  "  \"error\": \"BUSINESS_ERROR\",\n" +
                  "  \"message\": \"The technology name has not been found.\"\n" +
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
  public RouterFunction<ServerResponse> associateTechnologyWithCapacityRouter(Handler handler) {
    return route(POST(BASE_URL + "/technology/associate"), handler::associateTechnologyWithCapacity);
  }


  @Bean
  @RouterOperation(
    path = "/v1/api/technology/capacity/{capacityId}",
    method = RequestMethod.DELETE,
    operation = @Operation(
      operationId = "deleteTechnologiesByCapacity",
      summary = "Eliminar tecnologías por capacidad",
      description = "Elimina todas las tecnologías asociadas a una capacidad específica. " +
        "Si una tecnología solo está asociada a esa capacidad, se elimina completamente. " +
        "Si está asociada a otras capacidades, solo se elimina la relación.",
      tags = {"Technology Management"},
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
          name = "capacityId",
          description = "ID de la capacidad para la cual eliminar las tecnologías asociadas",
          required = true,
          in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
          schema = @Schema(type = "integer", format = "int64"),
          example = "1"
        )
      },
      responses = {
        @ApiResponse(
          responseCode = "200",
          description = "Tecnologías procesadas exitosamente",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(
              description = "Lista de IDs de tecnologías procesadas"
            ),
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Tecnologías procesadas correctamente",
              value = "[1, 2, 3]"
            )
          )
        ),
        @ApiResponse(
          responseCode = "400",
          description = "Error en la solicitud",
          content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error cuando el capacityId es nulo o no existe",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"Capacity ID cannot be null\"\n" +
                "}"
            )
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
  public RouterFunction<ServerResponse> deleteTechnologiesByCapacityRouter(Handler handler) {
    return route(DELETE(BASE_URL + "/technology/capacity/{capacityId}"), handler::deleteTechnologies);
  }
}