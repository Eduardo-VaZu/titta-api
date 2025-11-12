package com.titta.api.features.category.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import com.titta.api.features.category.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Gestión de categorias", description = "Endpoints para el CRUD completo de categorias.")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría de producto en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, la categoría ya existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
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
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoriaResponseDto>> obtenerCategorias() {
        List<CategoriaResponseDto> categorias = categoriaService.obterCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @Operation(summary = "Obtener una categoría por ID", description = "Devuelve los detalles de una categoría específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{idCategoria}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoriaResponseDto> getCategoriaById(
            @Parameter(description = "ID de la categoría a buscar")
            @PathVariable Long idCategoria) {

        CategoriaResponseDto categoria = categoriaService.getCategoriaById(idCategoria);
        return ResponseEntity.ok(categoria);
    }

    @Operation(summary = "Actualizar una categoría", description = "Actualiza el nombre de una categoría existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, el nuevo nombre ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{idCategoria}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponseDto> updateCategoria(
            @Parameter(description = "ID de la categoría a actualizar")
            @PathVariable Long idCategoria,
            @Valid @RequestBody CategoriaRequestDto categoriaDto) {

        CategoriaResponseDto categoriaActualizada = categoriaService.updateCategoria(idCategoria, categoriaDto);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría si no tiene productos asociados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, la categoría tiene productos asociados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{idCategoria}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteCategoria(
            @Parameter(description = "ID de la categoría a eliminar")
            @PathVariable Long idCategoria) {

        categoriaService.deleteCategoria(idCategoria);
        return ResponseEntity.noContent().build();
    }
}
