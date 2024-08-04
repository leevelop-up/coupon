package com.example.couponcore.model;

import com.example.couponcore.exception.CouponIssueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("발급수량과 바급 기간이 유효하면 발급 성공")
    void issue_1(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        coupon.issue();
        Assertions.assertEquals(coupon.getIssuedQuantity(),100);
    }
    @Test
    @DisplayName("발급수량ㅇ르 초과하면 예외 반환")
    void issue_2(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);


    }
    @Test
    @DisplayName("발급 기간이 아니면 예외 반환")
    void issue_3(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("최대 발급 수량이 설정되어 있지 않다면 true")
    void availableIssueQuantity_3(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(100)
                .build();

        boolean result = coupon.availableIssueQuantity();
        Assertions.assertTrue(result);
    }
    @Test
    @DisplayName("발급 수량이 남아있는 경ㅇ우 fasle")
    void availableIssueQuantity_2(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        boolean result = coupon.availableIssueQuantity();
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 수량이 남아있는 경우 true")
    void availableIssueQuantity_1(){
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        boolean result = coupon.availableIssueQuantity();
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기간이 종료되면 false")
    void availableIssueDate_3(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        boolean result = coupon.availableIssueDate();
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 기간에 해당되면 truw")
    void availableIssueDate_2(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(11))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        boolean result = coupon.availableIssueDate();
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기간이 시작되지 않았다면 false")
    void availableIssueDate_1(){
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        boolean result = coupon.availableIssueDate();
        Assertions.assertFalse(result);
    }
}