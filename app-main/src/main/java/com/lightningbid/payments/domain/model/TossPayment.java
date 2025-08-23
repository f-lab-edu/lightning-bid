package com.lightningbid.payments.domain.model;

import com.lightningbid.payments.domain.enums.TossPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TossPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toss_payment_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private TossPaymentStatus status;

    private BigDecimal totalAmount;

    private String method; // 결제 수단

    private String balanceAmount; // 취소할 수 있는 금액(잔고)

    private LocalDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보

    private LocalDateTime approvedAt; // 결제 승인이 일어난 날짜와 시간 정보

    private String failureCode;

    private String failureMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}

