package com.example.couponcore.service;

import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssue;
import com.example.couponcore.repository.mariadb.CouponIssueJpaRepository;
import com.example.couponcore.repository.mariadb.CouponIssueRepository;
import com.example.couponcore.repository.mariadb.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.couponcore.exception.ErrorCode.COUPON_ALREADY_ISSUED;
import static com.example.couponcore.exception.ErrorCode.COUPON_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class CouponIssueService {
    //repository bean 의존성 주입
    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;

    @Transactional
    public void issue(long couponId, long userId){
        //2개 이상의 쓰레드가 동시에 접근하지 못하도록 synchronized 블록으로 동기화 처리
        Coupon coupon = findCoupon(couponId); //쿠폰이 존재하는지 확인
        coupon.issue(); //쿠폰 검증후 발급수량 증가
        saveCouponIssue(couponId, userId);
    }
    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId){
        return couponJpaRepository.findById(couponId).orElseThrow(()->{
            throw new CouponIssueException(COUPON_NOT_EXIST,"쿠폰 정책이 존재하지 않습니다.%s".formatted(couponId));
        });
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssued(couponId, userId); //발급내역이 있는지 확인
        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(issue);
    }

    private void checkAlreadyIssued(long couponId, long userid){
        CouponIssue couponIssue = couponIssueRepository.findeFirstCouponIssue(couponId, userid);
        if(couponIssue != null){
            throw new CouponIssueException(COUPON_ALREADY_ISSUED,"이미 발급된 쿠폰입니다. userId:%s, couponId%s".formatted(userid,couponId));
        }
    }
}
