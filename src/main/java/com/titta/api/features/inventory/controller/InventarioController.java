package com.titta.api.features.inventory.controller;

import com.titta.api.features.inventory.dto.request.UpdateStockRequestDto;
import com.titta.api.features.inventory.dto.response.StockSedeResponseDto;
import com.titta.api.features.inventory.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/producto/{idProducto}/sede/{idSede}")
    @PreAuthorize("hasAuthority('AJUSTAR_INVENTARIO')")
    public ResponseEntity<StockSedeResponseDto> ajustarStock(
            @PathVariable Long idProducto,
            @PathVariable Long idSede,
            @Valid @RequestBody UpdateStockRequestDto stockDto) {
        return ResponseEntity.ok(inventarioService.ajustarStock(
                idProducto,
                idSede,
                stockDto
        ));
    }

    @GetMapping("/sede/{idSede}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<StockSedeResponseDto>> listarSedeId(@PathVariable Long idSede) {
        return ResponseEntity.ok(inventarioService.listarSedeId(idSede));
    }
}