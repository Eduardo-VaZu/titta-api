package com.titta.api.features.inventory.controller;

import com.titta.api.features.inventory.dto.request.UpdateStockRequestDto;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import com.titta.api.features.inventory.service.InventarioService;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Gestión de Inventario", description = "Endpoints para gestionar el stock de productos por sede.")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Ajustar stock de un producto en una sede (Vendedor/Admin)",
            description = "Suma o resta stock de un producto en una sede específica. Registra un movimiento de inventario.")
    @PostMapping("/producto/{idProducto}/sede/{idSede}/adjust")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<StockSedeResponseDto> ajustarStock(
            @PathVariable Long idProducto,
            @PathVariable Long idSede,
            @Valid @RequestBody UpdateStockRequestDto stockDto) {

        StockSedeResponseDto stockActualizado = inventarioService.ajustarStock(
                idProducto,
                idSede,
                stockDto.cantidad(),
                stockDto.razon()
        );

        return ResponseEntity.ok(stockActualizado);
    }
}
