package com.pragati.ecommerce.kafka;

import com.pragati.ecommerce.customer.CustomerResponse;
import com.pragati.ecommerce.order.PaymentMethod;
import com.pragati.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
