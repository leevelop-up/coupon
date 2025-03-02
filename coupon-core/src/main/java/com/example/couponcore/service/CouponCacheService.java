package com.example.couponcore.service;

import com.example.couponcore.model.Coupon;
import com.example.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {
    private final CouponIssueService couponIssueService;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    @Cacheable(cacheNames = "coupon",cacheManager = "localCacheManager")
    public CouponRedisEntity getCouponLocalCache(long couponId) {
        //로컬 캐시 없으면 레디스 캐시 조회
        return proxy().getCouponCache(couponId);
    }

    @CachePut(cacheNames="coupon")
    public CouponRedisEntity putCouponCache(long couponId){
        return getCouponCache(couponId);
    }
    @CachePut(cacheNames="coupon", cacheManager="localCacheManager")
    public CouponRedisEntity putCouponLocalCache(long couponId){
        return getCouponLocalCache(couponId);
    }
    private CouponCacheService proxy(){
        return (CouponCacheService) AopContext.currentProxy();
    }
}
