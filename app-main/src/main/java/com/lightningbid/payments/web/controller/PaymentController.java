package com.lightningbid.payments.web.controller;

import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.payments.service.PaymentService;
import com.lightningbid.payments.web.dto.request.PaymentConfirmRequestDto;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<CommonResponseDto<PaymentReadyResponseDto>> readyPayment(
            @Valid @RequestBody PaymentReadyRequestDto requestDto,
            @AuthenticationPrincipal CustomOAuth2User user) {

        PaymentReadyResponseDto responseDto = paymentService.readyDepositPayment(requestDto, user.getId());
        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "결제 준비가 완료 되었습니다.", responseDto));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CommonResponseDto<Void>> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequestDto requestDto,
            @AuthenticationPrincipal CustomOAuth2User user) {

        paymentService.confirmPayment(requestDto, user.getId());

        return ResponseEntity.ok(CommonResponseDto.success(HttpStatus.OK.value(), "결제가 완료 되었습니다."));
    }

//    @GetMapping("/fail")
//    public ResponseEntity<Void> handlePaymentFail(
//            @RequestParam String code,
//            @RequestParam String message,
//            @RequestParam String orderId,
//            @AuthenticationPrincipal CustomOAuth2User user) {
//
//        paymentService.processFailedPayment(code, message, orderId);
//
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create("http://localhost:3000/fail"))
//                .build();
//    }
}
