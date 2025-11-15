package com.titta.api.features.product.controller;

import com.titta.api.features.product.dto.request.ProductoBatchRequestDto;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.features.product.service.ProductoService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> crearProducto(
            @Valid @RequestBody ProductoRequestDto requestDto) {
        ProductoResponseDto nuevoProducto = productoService.crearProducto(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idProducto}")
                .buildAndExpand(nuevoProducto.idProducto())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(nuevoProducto);
    }

    @GetMapping("/{idProducto}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductoResponseDto> getProductoById(
            @PathVariable Long idProducto) {
        ProductoResponseDto producto = productoService.getProductoById(idProducto);
        return ResponseEntity.ok(producto);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductoResponseDto>> getAllProductos(
            @RequestParam(required = false) Boolean estado,
            @ParameterObject Pageable pageable) {
        Page<ProductoResponseDto> productosPage = productoService.getAllProductos(estado, pageable);
        return ResponseEntity.ok(productosPage);
    }

    @PutMapping("/{idProducto}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> updateProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoUpdateDto updateDto) {
        ProductoResponseDto producto = productoService.getProductoById(idProducto);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{idProducto}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProducto(
            @PathVariable Long idProducto) {
        productoService.deleteProducto(idProducto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ProductoResponseDto>> crearProductosBatch(
            @Valid @RequestBody ProductoBatchRequestDto batchRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoService.crearProductosBatch(batchRequestDto));
    }
}
