package com.example.couponcore.service;


import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {
    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final CouponCacheService couponCacheService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId){
       // CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId); // 레디스 캐시로 정보 조회
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId); // 레디스 캐시로 정보 조회
        //로컬 캐시로 정보 조회 하도록 변경
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId, coupon.TotalQuantity());

    }
    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity){
        if(totalIssueQuantity == null){
            redisRepository.issueRequest(couponId, userId, Integer.MAX_VALUE);
        }
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }
}
