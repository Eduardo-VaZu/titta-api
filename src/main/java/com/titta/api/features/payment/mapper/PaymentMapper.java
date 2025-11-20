package com.titta.api.features.payment.mapper;

import com.stripe.model.PaymentIntent;
import com.titta.api.features.payment.dto.PaymentIntentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentIntentDto toPaymentIntentDto(PaymentIntent paymentIntent) {
        if (paymentIntent == null) {
            return null;
        }

        return new PaymentIntentDto(
                paymentIntent.getId(),
                paymentIntent.getClientSecret(),
                paymentIntent.getAmount(),
                paymentIntent.getCurrency()
        );
    }
}
