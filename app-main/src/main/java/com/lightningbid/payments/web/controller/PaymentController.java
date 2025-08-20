package com.lightningbid.payments.web.controller;

import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.payments.service.PaymentService;
import com.lightningbid.payments.web.dto.request.PaymentReadyRequestDto;
import com.lightningbid.payments.web.dto.response.PaymentReadyResponseDto;
import com.lightningbid.common.dto.CommonResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<CommonResponseDto<PaymentReadyResponseDto>> readyPayments(
            @Valid @RequestBody PaymentReadyRequestDto requestDto,
            @AuthenticationPrincipal CustomOAuth2User user) {

        PaymentReadyResponseDto responseDto = paymentService.readyDepositPayment(requestDto, user.getId());
        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "결제 준비가 완료 되었습니다.", responseDto));
    }

    @GetMapping("/success")
    public ResponseEntity<CommonResponseDto<Void>> handlePaymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam BigDecimal amount) {

        paymentService.processSuccessfulPayment(paymentKey, orderId, amount);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/success"))
                .build();
    }

    @GetMapping("/fail")
    public ResponseEntity<Void> handlePaymentFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId) {

        paymentService.processFailedPayment(code, message, orderId);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/fail"))
                .build();
    }
}
