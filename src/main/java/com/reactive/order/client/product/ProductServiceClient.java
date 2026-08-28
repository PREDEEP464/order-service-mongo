package com.reactive.order.client.product;

import com.reactive.order.model.entity.response.ApiResponse;
import com.reactive.order.model.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public Mono<ProductResponse> getProductById(Long productId) {

        return webClientBuilder.build()
                .get()
                .uri(productServiceUrl + "/{id}", productId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                )
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }

    public Mono<ProductResponse> reserveProduct(
            Long productId,
            Integer quantity) {

        return webClientBuilder.build()
                .patch()
                .uri(
                        productServiceUrl + "/{id}/reserve/{quantity}",
                        productId,
                        quantity
                )
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                )
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }

    public Mono<ProductResponse> releaseProduct(
            Long productId,
            Integer quantity) {

        return webClientBuilder.build()
                .patch()
                .uri(
                        productServiceUrl + "/{id}/release/{quantity}",
                        productId,
                        quantity
                )
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                )
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }
}