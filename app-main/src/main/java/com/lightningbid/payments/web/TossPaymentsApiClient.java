package com.lightningbid.payments.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.common.config.RestTemplateConfig;
import com.lightningbid.common.config.properties.TossProperties;
import com.lightningbid.payments.exception.TossPaymentException;
import com.lightningbid.payments.web.dto.request.TossConfirmRequestDto;
import com.lightningbid.payments.web.dto.response.TossConfirmResponseDto;
import com.lightningbid.payments.web.dto.response.TossFailureResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class TossPaymentsApiClient {

    private final WebClient tossPaymentsWebClient;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final TossProperties tossProperties;

    public TossConfirmResponseDto confirmPayment(String paymentKey, String orderId, BigDecimal amount) {

        String encodedSecretKey = Base64.getEncoder().encodeToString((tossProperties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return tossPaymentsWebClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TossConfirmRequestDto.builder()
                        .paymentKey(paymentKey)
                        .orderId(orderId)
                        .amount(amount)
                        .build())
                .retrieve() // 요청 실행
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(TossConfirmResponseDto.class) // 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 대기
    }

    private Mono<Throwable> handleErrorResponse(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(TossFailureResponseDto.class)
                .flatMap(errorBody -> Mono.error(
                        new TossPaymentException(errorBody.getCode(), errorBody.getMessage())
                ));
    }


    public TossConfirmResponseDto confirmPaymentRestTemplate(String paymentKey, String orderId, BigDecimal amount) {

        HttpHeaders headers = new HttpHeaders();
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossProperties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        TossConfirmRequestDto requestDto = TossConfirmRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        HttpEntity<TossConfirmRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        try {

            ResponseEntity<TossConfirmResponseDto> responseEntity = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    requestEntity,
                    TossConfirmResponseDto.class
            );
            return responseEntity.getBody();

        } catch (HttpClientErrorException e) {

            try {

                String errorBodyAsString = e.getResponseBodyAsString();

                TossFailureResponseDto failureDto = objectMapper.readValue(errorBodyAsString, TossFailureResponseDto.class);

                throw new TossPaymentException(failureDto.getCode(), failureDto.getMessage());

            } catch (JsonProcessingException parseException) {
                throw new TossPaymentException("ERROR_PARSING_FAILED", e.getResponseBodyAsString());
            }

        }
    }
}
