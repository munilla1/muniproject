package com.model;


public record PaymentResponse(
        String paymentIntentId,
        Long amount,
        String currency,
        String status
) {
}