package com.titta.api.features.cart.controller;

import com.titta.api.config.exception.error.ErrorResponse;
import com.titta.api.features.cart.dto.request.AddToCartRequestDto;
import com.titta.api.features.cart.dto.response.CartResponseDto;
import com.titta.api.features.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Gestión de Carrito", description = "Endpoints para la gestión del carrito de compras del cliente.")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Añadir producto al carrito",
            description = "Añade un producto al carrito del usuario autenticado. " +
                    "Valida el stock y la sede. Si el carrito está vacío, lo vincula a la sede del producto. " +
                    "Si el producto ya existe, actualiza la cantidad. Requiere rol 'CLIENTE'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto añadido/actualizado en el carrito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. stock insuficiente, conflicto de sedes)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (Rol incorrecto)"),
            @ApiResponse(responseCode = "404", description = "Producto, Sede o Stock no encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/add")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<CartResponseDto> addItemToCart(
            @Valid @RequestBody AddToCartRequestDto requestDto) {

        CartResponseDto cartResponse = cartService.addItemToCart(requestDto);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }
}