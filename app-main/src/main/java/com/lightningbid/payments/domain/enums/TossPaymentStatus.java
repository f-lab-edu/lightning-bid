package com.lightningbid.payments.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TossPaymentStatus {

    READY("READY", "결제 생성 초기 상태"),
    IN_PROGRESS("IN_PROGRESS", "결제수단 인증 완료 상태"),
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT", "가상계좌 입금 대기 중"),
    DONE("DONE", "결제 승인 완료"),
    CANCELED("CANCELED", "승인된 결제가 취소된 상태"),
    PARTIAL_CANCELED("PARTIAL_CANCELED", "승인된 결제가 부분 취소된 상태"),
    ABORTED("ABORTED", "결제 승인 실패"),
    EXPIRED("EXPIRED", "결제 유효 시간이 지나 거래가 취소된 상태");

    private final String code;
    private final String description;
}
