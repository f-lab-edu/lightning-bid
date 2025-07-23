package com.lightningbid.api.payment.controller;

import com.lightningbid.api.payment.dto.request.DepositPreparationRequestDto;
import com.lightningbid.api.payment.dto.request.PaymentCompletionRequestDto;
import com.lightningbid.api.payment.dto.response.DepositPreparationResponseDto;
import com.lightningbid.api.payment.dto.response.PaymentCompletionResponseDto;
import com.lightningbid.api.user.dto.response.UserDto;
import com.lightningbid.common.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/")
public class PaymentController {

    @PostMapping("/{itemId}/deposits")
    public ResponseEntity<CommonResponseDto<DepositPreparationResponseDto>> prepareDepositPayment(
            @PathVariable long itemId,
            @Valid @RequestBody DepositPreparationRequestDto requestDto) {

        DepositPreparationResponseDto responseDto = DepositPreparationResponseDto.builder()
                .orderId("order_uuid_12345678")
                .itemId(itemId)
                .amount(requestDto.getAmount())
                .bidder(UserDto.builder()
                        .userId(1L)
                        .nickname("김토이")
                        .profileImageUrl("https://...")
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "결제 준비가 완료되었습니다.", responseDto)
        );
    }

    @PostMapping("/payments/complete")
    public ResponseEntity<CommonResponseDto<PaymentCompletionResponseDto>> confirmDepositPayment(@Valid @RequestBody PaymentCompletionRequestDto requestDto) {

        PaymentCompletionResponseDto responseDto = PaymentCompletionResponseDto.builder()
                .depositId("dep_z9y8x7w6")
                .orderId(requestDto.getOrderId())
                .itemId(123L)
                .amount(requestDto.getAmount())
                .paymentMethod("신한카드 (1234)")
                .paidAt(LocalDateTime.of(2025, 7, 20, 13, 50, 0))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(HttpStatus.CREATED.value(), "보증금 결제가 성공적으로 완료되었습니다.", responseDto)
        );
    }
}
