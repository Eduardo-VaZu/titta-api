package com.titta.api.features.cart.service;

import com.titta.api.features.cart.dto.request.AddToCartRequestDto;
import com.titta.api.features.cart.dto.request.UpdateCartItemRequestDto;
import com.titta.api.features.cart.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto addItemToCart(AddToCartRequestDto requestDto);

    CartResponseDto getActiveCart();

    CartResponseDto updateItemQuantity(Long idProducto, UpdateCartItemRequestDto requestDto);

    CartResponseDto removeItemFromCart(Long idProducto);

    CartResponseDto clearCart();
}