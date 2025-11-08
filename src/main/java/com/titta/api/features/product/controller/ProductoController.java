package com.titta.api.features.product.controller;

import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDetailsRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.product.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@Tag(name = "Gestión de Productos", description = "Endpoints para crear y obtener productos.")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto con su imagen, categoría y stock inicial por sede.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, el producto con ese SKU ya existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody ProductoRequestDto productoRequestDto) {
        ProductoResponseDto nuevoProducto = productoService.crearProducto(productoRequestDto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista con todos los productos existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductoResponseDto.class)))
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductoResponseDto>> obtenerTodosLosProductos() {
        List<ProductoResponseDto> productos = productoService.obtenerTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un producto por su ID", description = "Devuelve los detalles de un producto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductoResponseDto> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponseDto producto = productoService.obtenerProductoPorId(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar detalles de un producto (Admin)",
            description = "Permite al ADMIN actualizar campos clave del producto como precio, nombre, SKU y categoría.")
    @PutMapping("/{idProducto}/details")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> actualizarDetallesProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoUpdateDetailsRequestDto detailsDto) {

        ProductoResponseDto productoActualizado = productoService.actualizarDetalles(idProducto, detailsDto);
        return ResponseEntity.ok(productoActualizado);
    }

    @Operation(summary = "Desactivar un producto (Admin)",
            description = "Desactiva un producto para que no aparezca en la tienda, sin borrarlo.")
    @PostMapping("/{idProducto}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long idProducto) {
        productoService.cambiarEstado(idProducto, false);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activar un producto (Admin)",
            description = "Reactiva un producto para que vuelva a aparecer en la tienda.")
    @PostMapping("/{idProducto}/activate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> activarProducto(@PathVariable Long idProducto) {
        productoService.cambiarEstado(idProducto, true);
        return ResponseEntity.ok().build();
    }
}