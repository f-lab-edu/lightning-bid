package com.lightningbid.common.enums;

import com.lightningbid.auth.enums.TokenErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST: 잘못된 요청,
    COOKIE_REQUIRED(HttpStatus.BAD_REQUEST, "필수 쿠키가 누락되었습니다.", "COOKIE_EMPTY"),
    AUCTION_PERIOD_TOO_LONG(HttpStatus.BAD_REQUEST, "경매 종료일은 14일 이내여야 합니다.", null),
    AUCTION_PERIOD_TOO_SHORT(HttpStatus.BAD_REQUEST, "경매 기간은 최소 24시간 이상 이어야 합니다.", null),
    INSTANT_PRICE_BELOW_START(HttpStatus.BAD_REQUEST, "즉시 판매 가격은 경매 시작가보다 높아야 합니다.", null),
    INSTANT_PRICE_STEP_TOO_SMALL(HttpStatus.BAD_REQUEST, "즉시 판매 가격은 입찰 단위보다 높아야 합니다.", null),
    AUCTION_DURATION_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 기간 형식입니다.", null),

    // 401
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 쿠키에 존재하지 않습니다.", TokenErrorCode.REFRESH_TOKEN_MISSING.name()),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.", TokenErrorCode.REFRESH_TOKEN_EXPIRED.name()),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", TokenErrorCode.REFRESH_TOKEN_INVALID.name()),

    // 404 NOT_FOUND: 리소스를 찾을 수 없음
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "조회된 상품이 없습니다.", null),

    // 403 FORBIDDEN: 권한 없음,
    DEPOSIT_REQUIRED(HttpStatus.FORBIDDEN, "보증금 납부가 필요합니다.", null),

    // 409
    USER_ALREADY_REGISTERED(HttpStatus.FORBIDDEN, "이미 가입이 완료된 사용자입니다.", null),
    NICKNAME_DUPLICATE(HttpStatus.FORBIDDEN, "이미 사용 중인 닉네임입니다.", null);


    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
