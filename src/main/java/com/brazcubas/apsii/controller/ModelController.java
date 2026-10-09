package com.brazcubas.apsii.controller;

import com.brazcubas.apsii.model.ApiDtos;
import com.brazcubas.apsii.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints do catálogo de modelos preditivos.
 */
@RestController
@RequestMapping("/api/v1/models")
@Tag(name = "Models")
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @Operation(summary = "List models")
    @GetMapping
    public ApiDtos.ModelsResponse list() {
        return modelService.listActive();
    }

    @Operation(
            summary = "Get model",
            description = "Retorna os metadados de um modelo do catálogo pelo identificador. "
                    + "Modelos inativos também são retornados, com o campo active = false. "
                    + "Os dados vêm do catálogo estático data/models.json.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Modelo encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiDtos.ModelDetail.class),
                            examples = @ExampleObject(value = """
                                    {"id":"model-a","name":"Modelo A","description":"Modelo de previsão desenvolvido pelo Grupo 1.","groupName":"Grupo 1","algorithm":"Linear Regression","version":"1.0","active":true}
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Modelo não encontrado no catálogo (código MODEL_NOT_FOUND).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiDtos.ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"error":{"code":"MODEL_NOT_FOUND","message":"The requested prediction model was not found."}}
                                    """)))
    })
    @GetMapping("/{model_id}")
    public ApiDtos.ModelDetail get(
            @Parameter(
                    name = "model_id",
                    in = ParameterIn.PATH,
                    required = true,
                    description = "Identificador do modelo no catálogo. Use GET /api/v1/models para ver os IDs ativos.",
                    example = "model-a")
            @PathVariable("model_id") String modelId) {
        return modelService.get(modelId);
    }
}
