package com.example.couponcore.service;

import com.example.couponcore.TestConfig;
import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssue;
import com.example.couponcore.model.CouponType;
import com.example.couponcore.repository.mariadb.CouponIssueJpaRepository;
import com.example.couponcore.repository.mariadb.CouponIssueRepository;
import com.example.couponcore.repository.mariadb.CouponJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    CouponIssueService sut;
    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;
    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clean(){
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발급 내여이 존재하면 예외를 반환한다.")
    void saveCouponIssue_1(){
        //given
        CouponIssue issue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(issue);
        //when
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.saveCouponIssue(issue.getCouponId(),issue.getUserId()));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ALREADY_ISSUED);
        //then
    }
    @Test
    @DisplayName("쿠폰 발급 내역이 존재하지 않는다면 쿠폰을 발급한다.")
    void saveCouponIssue_2(){
        //given
        long couponId = 1L;
        long userId = 1L;
        //when
        CouponIssue result  = sut.saveCouponIssue(couponId, userId);
        Assertions.assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());
        //then
    }

    @Test
    @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다.")
    void issue_1(){
        //given
        long userId= 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        //when
        sut.issue(coupon.getId(),userId);
        //then
        Coupon couponResult = couponJpaRepository.findById(coupon.getId()).get();
        Assertions.assertEquals(couponResult.getIssuedQuantity(),1);

        CouponIssue couponIssueResult = couponIssueRepository.findeFirstCouponIssue(coupon.getId(), userId);
        Assertions.assertNotNull(couponIssueResult);
    }
    @Test
    @DisplayName("발급 수량에 문제가 있다면 예외를 반환")
    void issue_2(){
        //given
        long userId= 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        //when
        //then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(),userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }
    @Test
    @DisplayName("발급 기한에 문제가 있다면 예외를 반환")
    void issue_3(){
        //given
        long userId= 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        //when
        //then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(),userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }
    @Test
    @DisplayName("중복 발급 검증에 문제가 있다면 예외를 반환")
    void issue_4(){
        //given
        long userId= 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssue issue = CouponIssue.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(issue);
        //when
        //then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(coupon.getId(),userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ALREADY_ISSUED);
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않을때 예외를 반환")
    void issue_5(){
        //given
        long userId= 1;
        long couponId = 1;
        //when
        //then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issue(couponId,userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
    }

}