package com.lightningbid.payments.web.dto.response;

import com.lightningbid.payments.domain.enums.TossPaymentStatus;
import com.lightningbid.payments.domain.model.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TossConfirmResponseDto {

    private String orderId;

    private String paymentKey;

    private TossPaymentStatus status;

    private BigDecimal totalAmount;

    private String method; // 결제 수단

    private String balanceAmount; // 취소할 수 있는 금액(잔고)

    private LocalDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보

    private LocalDateTime approvedAt; // 결제 승인이 일어난 날짜와 시간 정보

    private TossFailureResponseDto failure;
}
