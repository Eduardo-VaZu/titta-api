package com.titta.api.features.sede.controller;

import com.titta.api.features.sede.dto.request.SedeRequestDto;
import com.titta.api.features.sede.dto.response.SedeResponseDto;
import com.titta.api.features.sede.service.SedeService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/sedes")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SedeResponseDto> createSede(
            @Valid @RequestBody SedeRequestDto sedeRequestDto) {
        SedeResponseDto nuevaSede = sedeService.crearSede(sedeRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idSede}")
                .buildAndExpand(nuevaSede.idSede())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(nuevaSede);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<SedeResponseDto>> getAllSedes(
            @RequestParam(required = false) Boolean estado,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(sedeService.obtenerTodasLasSedes(estado, pageable));
    }

    @GetMapping("/{idSede}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SedeResponseDto> getSedeById(
            @PathVariable Long idSede) {
        return ResponseEntity.ok(sedeService.getSedeById(idSede));
    }

    @PutMapping("/{idSede}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SedeResponseDto> updateSede(
            @PathVariable Long idSede,
            @Valid @RequestBody SedeRequestDto sedeDto) {
        return ResponseEntity.ok(sedeService.updateSede(idSede, sedeDto));
    }

    @DeleteMapping("/{idSede}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteSede(
            @PathVariable Long idSede) {
        sedeService.deleteSede(idSede);
        return ResponseEntity.noContent().build();
    }
}