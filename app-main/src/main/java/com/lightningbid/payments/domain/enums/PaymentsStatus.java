package com.lightningbid.payments.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentsStatus {

    READY("결제 준비 (주문 생성)"),
    PAID("결제 완료"),
    FAILED("결제 실패"),
    CANCELED("결제 취소 (환불)");

    private final String description;
}
