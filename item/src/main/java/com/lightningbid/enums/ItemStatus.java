package com.lightningbid.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatus {

    ACTIVE("ACTIVE", "판매중", "경매에 등록되어 입찰 가능한 상태"),
    PENDING("PENDING", "거래진행중", "낙찰 후 결제, 배송 등 실제 거래가 진행 중인 상태"),
    COMPLETED("COMPLETED", "거래완료", "모든 거래 과정이 성공적으로 완료된 상태"),
    CANCELED("CANCELED", "취소", "판매자에 의해 경매가 취소된 상태"),
    EXPIRED("EXPIRED", "기간만료", "경매 기간이 종료되었으나 낙찰자가 없는 상태"),
    FAILED("FAILED", "거래불발", "낙찰 후 거래가 최종적으로 성사되지 못한 상태");

    private final String code;
    private final String koreanCode;
    private final String description;
}