package com.titta.api.features.inventory.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.inventory.dto.request.UpdateStockRequestDto;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import com.titta.api.features.inventory.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Gestión de Inventario", description = "Endpoints para ajustar y consultar el stock de productos.")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Ajustar stock de un producto en una sede",
            description = "Suma o resta stock de un producto en una sede específica (ej. cantidad: 5 para sumar, cantidad: -3 para restar). " +
                    "Registra un movimiento de inventario de tipo 'AJUSTE_MANUAL'. " +
                    "Requiere rol 'ADMINISTRADOR' o 'EMPLEADO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock ajustado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockSedeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. ajuste resultaría en stock negativo)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (Rol incorrecto)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto, Sede o el Stock para esa combinación no encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/producto/{idProducto}/sede/{idSede}")
    @PreAuthorize("hasAnyRole('ADMINISTRADORA', 'EMPLEADO')")
    public ResponseEntity<StockSedeResponseDto> ajustarStock(
            @Parameter(description = "ID del producto a ajustar", required = true)
            @PathVariable Long idProducto,

            @Parameter(description = "ID de la sede donde se ajustará el stock", required = true)
            @PathVariable Long idSede,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "La cantidad a ajustar (positivo para sumar, negativo para restar) y la razón del ajuste.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateStockRequestDto.class)))
            @Valid @RequestBody UpdateStockRequestDto stockDto) {

        StockSedeResponseDto stockActualizado = inventarioService.ajustarStock(
                idProducto,
                idSede,
                stockDto.cantidad(),
                stockDto.razon()
        );

        return ResponseEntity.ok(stockActualizado);
    }

    @Operation(summary = "Obtener el stock de todos los productos de una sede",
            description = "Devuelve una lista de todos los productos y su stock disponible para una sede específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock de la sede obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = StockSedeResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/sede/{idSede}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<StockSedeResponseDto>> listarSedeId(
            @Parameter(description = "ID de la sede a consultar", required = true)
            @PathVariable Long idSede) {

        List<StockSedeResponseDto> stockSede = inventarioService.listarSedeId(idSede);
        return ResponseEntity.ok(stockSede);
    }
}
