package com.example.couponcore.service;


import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
import com.example.couponcore.repository.redis.RedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.couponcore.utill.CouponRedisUtils.getIssueRequestKey;
import static com.example.couponcore.utill.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {
    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId){
        Coupon coupon = couponIssueService.findCoupon(couponId);
        if(!coupon.availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,"쿠폰 발급 기간이 아닙니다.");
        }
        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000,3000, ()->{
            //쿠폰 존재 확인
            if(!couponIssueRedisService.availableTotalIssueQuantity(couponId, coupon.getTotalQuantity())){
                throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,"쿠폰 발급 가능 수량 초과");
            }
            //중복발급 확인
            if(!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)){
                throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,"이미 발급된 쿠폰입니다.");
            }
            issueCoupon(couponId, userId);
        });
    }
    private void issueCoupon(long couponId, long userId){
        CouponIssueRequest IssueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(IssueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));//요청을 추가
            redisRepository.rPush(getIssueRequestQueueKey(), value);//큐에 적재 이후 적재된 내역으로 쿠폰 발급 처리
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"쿠폰 발급 요청 오류");
        }
;
        //큐 적재
    }

}
