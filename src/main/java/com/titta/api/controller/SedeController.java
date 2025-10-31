package com.titta.api.controller;

import com.titta.api.dto.request.SedeRequestDto;
import com.titta.api.dto.response.SedeResponseDto;
import com.titta.api.exception.error.ErrorResponse;
import com.titta.api.service.SedeService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/sedes")
@Tag(name = "Gestión de Sedes", description = "Endpoints para crear y obtener sedes.")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @Operation(summary = "Crear una nueva sede", description = "Crea una nueva sede con su dirección y horarios de operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sede creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, la sede ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SedeResponseDto> crearSede(@Valid @RequestBody SedeRequestDto sedeRequestDto) {
        SedeResponseDto nuevaSede = sedeService.crearSede(sedeRequestDto);
        return new ResponseEntity<>(nuevaSede, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las sedes", description = "Devuelve una lista con todas las sedes existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sedes obtenida con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = SedeResponseDto.class)))
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SedeResponseDto>> obtenerTodasLasSedes() {
        List<SedeResponseDto> sedes = sedeService.obtenerTodasLasSedes();
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }
}