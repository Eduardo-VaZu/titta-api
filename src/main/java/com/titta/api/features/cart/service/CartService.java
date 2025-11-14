package com.titta.api.features.cart.service;

import com.titta.api.features.cart.dto.request.AddToCartRequestDto;
import com.titta.api.features.cart.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto addItemToCart(AddToCartRequestDto requestDto);

    // Aquí añadiremos (después) los otros métodos:
    // CartResponseDto getActiveCart();
    // CartResponseDto updateItemQuantity(Long idProducto, Integer cantidad);
    // CartResponseDto removeItemFromCart(Long idProducto);
    // void clearCart();
}