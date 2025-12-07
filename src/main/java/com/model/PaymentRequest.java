package com.model;

public record PaymentRequest(
		Long productoId,
        String description,
        String currency,
        String stripeEmail,
        String paymentMethodId
) {
}