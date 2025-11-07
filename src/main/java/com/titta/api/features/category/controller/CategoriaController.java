package com.titta.api.features.category.controller;

import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.category.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría de producto en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(
                    responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409", description = "Conflicto, la categoría ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDto> crearCategoria(@Valid @RequestBody CategoriaRequestDto categoriaRequestDto) {
        CategoriaResponseDto nuevaCategoria = categoriaService.crearCategoria(categoriaRequestDto);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve una lista con todas las categorías existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CategoriaResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> obtenerCategorias() {
        List<CategoriaResponseDto> categorias = categoriaService.obterCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }
}
