package com.lightningbid.file.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileDomain {

    PROFILE("profile/"),
    ITEM("item/");

    private final String path;
}
