package com.pragati.ecommerce.payment;

import com.pragati.ecommerce.customer.CustomerResponse;
import com.pragati.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
