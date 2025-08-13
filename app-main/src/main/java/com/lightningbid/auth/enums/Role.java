package com.lightningbid.auth.enums;

public enum Role {
    // Spring Security는 권한 코드에 항상 "ROLE_" 접두사가 붙어야 한다.
    ROLE_GUEST,
    ROLE_USER,
    ROLE_ADMIN;
}
