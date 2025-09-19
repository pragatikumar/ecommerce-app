package com.pragati.ecommerce.order;

import com.pragati.ecommerce.customer.CustomerClient;
import com.pragati.ecommerce.exception.BusinessException;
import com.pragati.ecommerce.kafka.OrderConfirmation;
import com.pragati.ecommerce.kafka.OrderProducer;
import com.pragati.ecommerce.orderline.OrderLineRequest;
import com.pragati.ecommerce.orderline.OrderLineService;
import com.pragati.ecommerce.payment.PaymentClient;
import com.pragati.ecommerce.payment.PaymentRequest;
import com.pragati.ecommerce.product.ProductClient;
import com.pragati.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;

    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        //Use FeignClient
        var customer = this.customerClient.findCustomerById(request.customerId())
                          .orElseThrow(()-> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        //Use RestTemplate
        var purchasedProducts = productClient.purchaseProducts(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // todo start payment process
        var paymentRequest = new PaymentRequest(request.amount(),
                request.paymentMethod(),order.getId(),order.getReference(),customer);

        paymentClient.requestOrderPayment(paymentRequest);

        //Send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {

        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(()-> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
