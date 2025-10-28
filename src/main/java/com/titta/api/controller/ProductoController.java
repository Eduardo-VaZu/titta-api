package com.titta.api.controller;

import com.titta.api.dto.request.ProductoRequestDto;
import com.titta.api.dto.response.ProductoResponseDto;
import com.titta.api.exception.error.ErrorResponse;
import com.titta.api.service.ProductoService;
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
    public ResponseEntity<ProductoResponseDto> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponseDto producto = productoService.obtenerProductoPorId(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }
}