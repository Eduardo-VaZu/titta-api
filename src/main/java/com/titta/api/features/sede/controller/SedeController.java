package com.titta.api.features.sede.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.sede.dto.request.SedeRequestDto;
import com.titta.api.features.sede.dto.response.SedeResponseDto;
import com.titta.api.features.sede.service.SedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sedes")
@Tag(name = "4. Gestión de Sedes", description = "Endpoints para el CRUD completo de sedes (tiendas).")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @Operation(summary = "Crear una nueva sede",
            description = "Crea una nueva sede con su dirección y horarios de operación. Requiere rol 'ADMINISTRADOR'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sede creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Rol incorrecto)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, la sede ya existe (nombre duplicado)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SedeResponseDto> crearSede(@Valid @RequestBody SedeRequestDto sedeRequestDto) {
        SedeResponseDto nuevaSede = sedeService.crearSede(sedeRequestDto);
        return new ResponseEntity<>(nuevaSede, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las sedes (Paginado)",
            description = "Devuelve una lista paginada de todas las sedes. Permite filtrar por estado (activas/inactivas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sedes obtenida con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<SedeResponseDto>> obtenerTodasLasSedes(
            @Parameter(description = "Filtrar por estado (true = activas, false = inactivas)")
            @RequestParam(required = false) Boolean estado,

            @ParameterObject
            Pageable pageable) {

        Page<SedeResponseDto> sedes = sedeService.obtenerTodasLasSedes(estado, pageable);
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }

    @Operation(summary = "Obtener una sede por ID",
            description = "Devuelve los detalles completos de una sede específica, incluyendo dirección y horarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{idSede}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SedeResponseDto> getSedeById(
            @Parameter(description = "ID de la sede a buscar", required = true)
            @PathVariable Long idSede) {

        SedeResponseDto sede = sedeService.getSedeById(idSede);
        return ResponseEntity.ok(sede);
    }

    @Operation(summary = "Actualizar una sede",
            description = "Actualiza los datos de una sede existente (nombre, dirección, horarios, etc.). Requiere rol 'ADMINISTRADOR'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Rol incorrecto)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, el nuevo nombre ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{idSede}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SedeResponseDto> updateSede(
            @Parameter(description = "ID de la sede a actualizar", required = true)
            @PathVariable Long idSede,
            @Valid @RequestBody SedeRequestDto sedeDto) {

        SedeResponseDto sedeActualizada = sedeService.updateSede(idSede, sedeDto);
        return ResponseEntity.ok(sedeActualizada);
    }

    @Operation(summary = "Desactivar una sede (Borrado Lógico)",
            description = "Realiza un borrado lógico de la sede (cambia su estado a 'false'). No la borra de la BD. Requiere rol 'ADMINISTRADOR'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sede desactivada exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "403", description = "No autorizado (Rol incorrecto)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{idSede}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteSede(
            @Parameter(description = "ID de la sede a desactivar", required = true)
            @PathVariable Long idSede) {

        sedeService.deleteSede(idSede);
        return ResponseEntity.noContent().build();
    }
}