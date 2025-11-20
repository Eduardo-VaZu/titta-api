package com.titta.api.features.payment.service;

import com.stripe.exception.StripeException;
import com.titta.api.features.payment.dto.PaymentIntentDto;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentIntentDto createPaymentIntent(BigDecimal amount) throws StripeException;
}