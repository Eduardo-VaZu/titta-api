package com.titta.api.features.product.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.product.dto.request.ProductoBatchRequestDto;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.features.product.service.ProductoService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Gestión de Productos", description = "Endpoints para crear y obtener productos.")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Crear un nuevo producto",
            description = "Crea un producto con su imagen y stock inicial por sede.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoResponseDto.class))),
            @ApiResponse(
                    responseCode = "400", description = "Datos de entrada inválidos (DTO inválido)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "Recurso no encontrado (Categoría o Sede no existen)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409", description = "Conflicto (SKU ya existe)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> crearProducto(
            @Valid @RequestBody ProductoRequestDto requestDto) {
        ProductoResponseDto nuevoProducto = productoService.crearProducto(requestDto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un producto por ID",
            description = "Devuelve los detalles completos de un producto, incluyendo imagen y stocks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{idProducto}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductoResponseDto> getProductoById(
            @PathVariable Long idProducto) {
        ProductoResponseDto producto = productoService.getProductoById(idProducto);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Obtener todos los productos",
            description = "Devuelve una lista paginada de todos los productos. " +
                    "Soporta filtrado por estado, paginación y ordenamiento. " +
                    "Ejemplo: ?page=0&size=10&sort=precio,desc&estado=true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos paginada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))})
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductoResponseDto>> getAllProductos(
            @Parameter(description = "Filtrar por estado (true = activos, false = inactivos)")
            @RequestParam(required = false) Boolean estado,
            @ParameterObject Pageable pageable) {
        Page<ProductoResponseDto> productosPage = productoService.getAllProductos(estado, pageable);
        return ResponseEntity.ok(productosPage);
    }

    @Operation(summary = "Actualizar un producto",
            description = "Actualiza los datos principales de un producto (nombre, precio, etc.). No actualiza imagen ni stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto o Categoría no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{idProducto}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDto> updateProducto(
            @PathVariable Long idProducto, @Valid @RequestBody ProductoUpdateDto updateDto) {
        ProductoResponseDto productoActualizado = productoService.updateProducto(idProducto, updateDto);
        return ResponseEntity.ok(productoActualizado);
    }

    @Operation(summary = "Desactivar un producto (Soft Delete)",
            description = "Realiza un borrado lógico del producto, estableciendo su estado a 'false'. No lo elimina de la base de datos.")
    // <-- MODIFICADO
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{idProducto}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProducto(
            @PathVariable Long idProducto) {
        productoService.deleteProducto(idProducto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Crear múltiples productos (Batch)",
            description = "Crea una lista de productos en una sola transacción. Valida todos los SKUs, Categorías y Sedes antes de guardar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Productos creados exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (DTO inválido, SKUs duplicados en el request)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado (Alguna Categoría o Sede no existe)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto (Alguno de los SKUs ya existe en la BD)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ProductoResponseDto>> crearProductosBatch(
            @Valid @RequestBody ProductoBatchRequestDto batchRequestDto) {
        List<ProductoResponseDto> nuevosProductos = productoService.crearProductosBatch(batchRequestDto);
        return new ResponseEntity<>(nuevosProductos, HttpStatus.CREATED);
    }
}
