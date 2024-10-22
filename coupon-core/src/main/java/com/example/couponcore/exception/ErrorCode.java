package com.example.couponcore.exception;

public enum ErrorCode {

    INVALID_COUPON_ISSUE_QUANTITY("쿠폰 발급 수량이 유효하지 않음"),
    INVALID_COUPON_ISSUE_DATE("쿠폰 발급 기간이 유효하지 않음"),
    COUPON_NOT_EXIST("존재하지 않는 쿠폰"),
    COUPON_ALREADY_ISSUED("이미 발급된 쿠폰"),
    DUPLICATED_COUPON_ISSUE("이미 발급된 쿠폰입니다."),
    FAIL_COUPON_ISSUE_REQUEST("쿠폰 발급 요청에 실패");

    public final String message;

    ErrorCode(String message) {
        this.message = message;
    }


}
