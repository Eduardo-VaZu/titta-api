package com.titta.api.features.cart.controller;

import com.titta.api.features.cart.dto.request.AddToCartRequestDto;
import com.titta.api.features.cart.dto.request.UpdateCartItemRequestDto;
import com.titta.api.features.cart.dto.response.CartResponseDto;
import com.titta.api.features.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<CartResponseDto> addItemToCart(
            @Valid @RequestBody AddToCartRequestDto requestDto) {
        return ResponseEntity.ok(cartService.addItemToCart(requestDto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<CartResponseDto> getActiveCart() {
        return ResponseEntity.ok(cartService.getActiveCart());
    }

    @PutMapping("/item/{idProducto}")
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable Long idProducto,
            @Valid @RequestBody UpdateCartItemRequestDto requestDto) {
        return ResponseEntity.ok(cartService.updateItemQuantity(idProducto, requestDto));
    }

    @DeleteMapping("/item/{idProducto}")
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<CartResponseDto> removeItemFromCart(@PathVariable Long idProducto) {
        return ResponseEntity.ok(cartService.removeItemFromCart(idProducto));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('USAR_CARRITO')")
    public ResponseEntity<CartResponseDto> clearCart() {
        return ResponseEntity.ok(cartService.clearCart());
    }
}