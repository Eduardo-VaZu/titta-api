package com.titta.api.features.sale.controller;

import com.titta.api.features.sale.dto.request.SaleRequestDto;
import com.titta.api.features.sale.dto.response.SaleResponseDto;
import com.titta.api.features.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService ventaService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<SaleResponseDto> checkout(@Valid @RequestBody SaleRequestDto request) {
        return ResponseEntity.ok(ventaService.realizarVenta(request));
    }
}