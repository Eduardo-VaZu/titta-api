package com.titta.api.features.category.controller;

import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import com.titta.api.features.category.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponseDto> crearCategoria(
            @Valid @RequestBody CategoriaRequestDto categoriaRequestDto) {
        CategoriaResponseDto nuevaCategoria = categoriaService.crearCategoria(categoriaRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idCategoria}")
                .buildAndExpand(categoriaRequestDto.nombreCategoria())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(nuevaCategoria);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoriaResponseDto>> getAllCategorias() {
        return ResponseEntity.ok(categoriaService.obterCategorias());
    }


    @GetMapping("/{idCategoria}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoriaResponseDto> getCategoriaById(
            @PathVariable Long idCategoria) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(idCategoria));
    }

    @PutMapping("/{idCategoria}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponseDto> updateCategoria(
            @PathVariable Long idCategoria,
            @Valid @RequestBody CategoriaRequestDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.updateCategoria(idCategoria, categoriaDto));
    }

    @DeleteMapping("/{idCategoria}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteCategoria(
            @PathVariable Long idCategoria) {
        categoriaService.deleteCategoria(idCategoria);
        return ResponseEntity.noContent().build();
    }
}
