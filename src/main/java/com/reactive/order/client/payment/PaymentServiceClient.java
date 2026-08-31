package com.reactive.order.client.payment;

import com.reactive.order.model.entity.response.ApiResponse;
import com.reactive.order.model.entity.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final WebClient webClient;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public Mono<PaymentResponse> getPaymentByOrderId(String orderId) {

        return webClient
                .get()
                .uri(paymentServiceUrl + "/order/" + orderId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                )
                .bodyToMono(
                        new ParameterizedTypeReference<
                                ApiResponse<PaymentResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }
}