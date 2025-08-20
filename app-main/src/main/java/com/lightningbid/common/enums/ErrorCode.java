package com.lightningbid.common.enums;

import com.lightningbid.auth.enums.TokenErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST: 잘못된 요청
    COOKIE_REQUIRED(HttpStatus.BAD_REQUEST, "필수 쿠키가 누락되었습니다.", "COOKIE_EMPTY"),
    AUCTION_PERIOD_TOO_LONG(HttpStatus.BAD_REQUEST, "경매 종료일은 14일 이내여야 합니다.", null),
    AUCTION_PERIOD_TOO_SHORT(HttpStatus.BAD_REQUEST, "경매 기간은 최소 24시간 이상 이어야 합니다.", null),
    BID_UNIT_OVER_INSTANT_SALE(HttpStatus.BAD_REQUEST, "입찰 단위는 즉시 판매 가격보다 낮아야 합니다.", null),
    BID_UNIT_NOT_INSTANT_MULTIPLE(HttpStatus.BAD_REQUEST, "즉시 판매 가격이 입찰 단위와 맞지 않습니다. 즉시 판매 가격은, 경매 시작 금액에 입찰 단위의 배수를 더한 값으로 입력해 주세요.", null),
    INSTANT_PRICE_BELOW_START(HttpStatus.BAD_REQUEST, "즉시 판매 가격은 경매 시작가보다 높아야 합니다.", null),
    INSTANT_PRICE_STEP_TOO_SMALL(HttpStatus.BAD_REQUEST, "즉시 판매 가격은 (경매 시작 가격 + 입찰 단위) 보다 높은 금액 이어야 합니다.", null),
    AUCTION_DURATION_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 기간 형식입니다.", null),
    BID_UNIT_TOO_SMALL(HttpStatus.BAD_REQUEST, "최소 입찰 단위보다 큰 금액으로 입찰 선청이 가능합니다.", null),
    BID_PRICE_TOO_LOW(HttpStatus.BAD_REQUEST, "판매 시작가 혹은 최고 입찰가 이하의 금액을 입력 하였습니다.", null),
    BID_OVER_INSTANT(HttpStatus.BAD_REQUEST, "입찰 금액이 즉시구매가보다 높습니다.", null),
    AUCTION_ITEM_INACTIVE(HttpStatus.BAD_REQUEST, "판매 중인 제품에만 입찰에 참여하실 수 있습니다", null),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다.", null),

    // 401
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 쿠키에 존재하지 않습니다.", TokenErrorCode.REFRESH_TOKEN_MISSING.name()),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.", TokenErrorCode.REFRESH_TOKEN_EXPIRED.name()),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프래시 토큰입니다.", TokenErrorCode.REFRESH_TOKEN_INVALID.name()),
    SIGN_UP_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "회원가입 토큰이 쿠키에 존재하지 않습니다.", TokenErrorCode.SIGN_UP_TOKEN_MISSING.name()),
    SIGN_UP_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "회원가입 토큰이 만료되었습니다.", TokenErrorCode.SIGN_UP_TOKEN_EXPIRED.name()),
    SIGN_UP_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 회원가입 토큰입니다.", TokenErrorCode.SIGN_UP_TOKEN_INVALID.name()),
    SIGN_IN_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "로그인 토큰이 쿠키에 존재하지 않습니다.", TokenErrorCode.SIGN_IN_TOKEN_MISSING.name()),
    SIGN_IN_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 토큰이 만료되었습니다.", TokenErrorCode.SIGN_IN_TOKEN_EXPIRED.name()),
    SIGN_IN_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 로그인 토큰입니다.", TokenErrorCode.SIGN_IN_TOKEN_INVALID.name()),
    ACCESS_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", TokenErrorCode.TOKEN_INVALID.name()),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다.", TokenErrorCode.TOKEN_INVALID.name()),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다.", TokenErrorCode.TOKEN_EXPIRED.name()),
    ACCESS_TOKEN_FORBIDDEN(HttpStatus.UNAUTHORIZED, "접근 권한이 없는 토큰입니다.", TokenErrorCode.TOKEN_FORBIDDEN.name()),

    // 404 NOT_FOUND: 리소스를 찾을 수 없음
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "제품 정보를 찾을 수 없습니다.", null),
    BID_NOT_FOUND(HttpStatus.NOT_FOUND, "입찰을 찾을 수 없습니다.", null),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "경매 정보를 찾을 수 없습니다.", null),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.", null),
    CATEGORY_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 ID 입니다", null),

    // 403 FORBIDDEN: 권한 없음,
    DEPOSIT_REQUIRED(HttpStatus.FORBIDDEN, "보증금 납부가 필요합니다.", null),

    // 409
    USER_ALREADY_REGISTERED(HttpStatus.FORBIDDEN, "이미 가입이 완료된 사용자입니다.", null),
    NICKNAME_DUPLICATE(HttpStatus.FORBIDDEN, "이미 사용 중인 닉네임입니다.", null),

    // 415
    FILE_NOT_IMAGE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "이미지 파일만 업로드할 수 있습니다.", null),

    // 500
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.", null)
    ;
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
