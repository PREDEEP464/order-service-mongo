package com.reactive.order.serviceImpl;

import com.reactive.order.client.product.ProductServiceClient;
import com.reactive.order.client.payment.PaymentServiceClient;
import com.reactive.order.model.entity.response.OrderPaymentResponse;
import com.reactive.order.dao.api.OrderRepository;
import com.reactive.order.exception.OrderNotFoundException;
import com.reactive.order.model.entity.Order;
import com.reactive.order.model.entity.OrderStatus;
import com.reactive.order.model.entity.request.OrderRequest;
import com.reactive.order.model.entity.response.OrderResponse;
import com.reactive.order.model.entity.response.PaymentResponse;
import com.reactive.order.model.response.ProductResponse;
import com.reactive.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest request) {

        return Mono.defer(() ->
                        productServiceClient.getProductById(request.getProductId())
                                .onErrorResume(
                                        WebClientResponseException.NotFound.class,
                                        ex -> Mono.error(
                                                new IllegalArgumentException(
                                                        "No available product with ID: "
                                                                + request.getProductId()
                                                )
                                        )
                                )
                )
                .filter(product ->
                        Boolean.TRUE.equals(product.getIsActive())
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "No available product with ID: "
                                                + request.getProductId()
                                )
                        )
                )
                .filter(product ->
                        product.getAvailableQuantity() >= request.getQuantity()
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "Insufficient product quantity for product ID: "
                                                + request.getProductId()
                                )
                        )
                )
                .flatMap(product ->
                        productServiceClient
                                .reserveProduct(
                                        request.getProductId(),
                                        request.getQuantity()
                                )
                                .map(reservedProduct ->
                                        buildOrder(request, reservedProduct)
                                )
                )
                .flatMap(orderRepository::save)
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Order created successfully: " + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while creating order: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Flux<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Order fetched: " + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while fetching orders: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<OrderResponse> getOrderById(String id) {

        return orderRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Order fetched: " + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while fetching order: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<OrderResponse> cancelOrder(String id) {

        return orderRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        )
                )
                .flatMap(order ->
                        productServiceClient.releaseProduct(
                                        order.getProductId(),
                                        order.getQuantity()
                                )
                                .then(
                                        Mono.defer(() -> {
                                            order.setStatus(OrderStatus.CANCELLED);
                                            order.setUpdatedAt(LocalDateTime.now());

                                            return orderRepository.save(order);
                                        })
                                )
                )
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Order cancelled: " + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while cancelling order: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<OrderResponse> confirmOrder(String id) {

        return orderRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        )
                )
                .filter(order ->
                        order.getStatus()
                                == OrderStatus.PAYMENT_PENDING
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "Order cannot be confirmed in its current state"
                                )
                        )
                )
                .flatMap(order -> {
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.setUpdatedAt(LocalDateTime.now());

                    return orderRepository.save(order);
                })
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Order confirmed: " + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while confirming order: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<OrderResponse> markPaymentFailed(String id) {

        return orderRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        )
                )
                .filter(order ->
                        order.getStatus()
                                == OrderStatus.PAYMENT_PENDING
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "Payment cannot be failed for this order"
                                )
                        )
                )
                .flatMap(order ->
                        productServiceClient
                                .releaseProduct(
                                        order.getProductId(),
                                        order.getQuantity()
                                )
                                .thenReturn(order)
                )
                .flatMap(order -> {
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                    order.setUpdatedAt(LocalDateTime.now());

                    return orderRepository.save(order);
                })
                .map(this::convertToResponse)
                .doOnNext(order ->
                        System.out.println(
                                "Payment failed for order: "
                                        + order.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while marking payment failed: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<OrderPaymentResponse> getOrderPaymentDetails(String id) {

        Mono<OrderResponse> orderMono =
                getOrderById(id);

        Mono<PaymentResponse> paymentMono =
                paymentServiceClient.getPaymentByOrderId(id);

        return Mono.zip(orderMono, paymentMono)
                .flatMap(tuple -> {

                    OrderResponse order = tuple.getT1();
                    PaymentResponse payment = tuple.getT2();

                    return Mono.just(
                            new OrderPaymentResponse(
                                    order.getId(),
                                    order.getCustomerName(),
                                    order.getProductId(),
                                    order.getQuantity(),
                                    order.getTotalAmount(),
                                    order.getStatus().name(),
                                    payment.getId(),
                                    payment.getAmount(),
                                    payment.getStatus()
                            )
                    );
                })
                .doOnNext(details ->
                        System.out.println(
                                "Order and payment details fetched: "
                                        + details.getOrderId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while fetching order/payment details: "
                                        + error.getMessage()
                        )
                );
    }

    private Order buildOrder(
            OrderRequest request,
            ProductResponse product) {

        BigDecimal totalAmount =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(product.getPrice());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        return order;
    }

    private OrderResponse convertToResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getProductId(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}