package com.example.couponcore.service;


import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.exception.CouponIssueException;
import com.example.couponcore.exception.ErrorCode;
import com.example.couponcore.model.Coupon;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
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
    private final CouponCacheService couponCacheService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId){
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();
        //lock을 걸고 해제하는 부분이 원인이 될 수 있다.

        couponIssueRedisService.checkCouponIssueQuantity(coupon,userId);
        issueCoupon(couponId, userId);

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
