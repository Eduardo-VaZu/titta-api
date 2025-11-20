package com.titta.api.features.payment.dto;

public record PaymentIntentDto(
        String id,
        String clientSecret,
        long amount,
        String currency
) {
}