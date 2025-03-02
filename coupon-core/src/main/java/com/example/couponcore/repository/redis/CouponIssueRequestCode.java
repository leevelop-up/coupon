package com.example.couponcore.repository.redis;

import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.exception.ErrorCode;

public enum CouponIssueRequestCode {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);

    CouponIssueRequestCode(int code){

    }

    public static CouponIssueRequestCode find(String code){
        int codeValue = Integer.parseInt(code);
        return switch (codeValue) {
            case 1 -> SUCCESS;
            case 2 -> DUPLICATED_COUPON_ISSUE;
            case 3 -> INVALID_COUPON_ISSUE_QUANTITY;
            default -> throw new IllegalArgumentException("Unexpected value: " + code);
        };
    }

    public static void checkRequestResult(CouponIssueRequestCode code){
        if(code == DUPLICATED_COUPON_ISSUE){
            throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급된 쿠폰입니다.");
        }
        if(code == INVALID_COUPON_ISSUE_QUANTITY){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "쿠폰 발급 요청 수량이 초과되었습니다.");
        }
    }

}
