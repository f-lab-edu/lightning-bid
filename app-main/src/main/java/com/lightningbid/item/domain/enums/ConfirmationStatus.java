package com.lightningbid.item.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfirmationStatus {

    PENDING("PENDING", "확정 대기 중"),
    CONFIRMED("CONFIRMED", "확정 완료"),
    REJECTED("REJECTED", "확정 거부");

    private final String code;
    private final String description;
}
