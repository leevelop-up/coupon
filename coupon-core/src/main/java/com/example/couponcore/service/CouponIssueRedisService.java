package com.example.couponcore.service;

import com.example.couponcore.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.couponcore.utill.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {
    private final RedisRepository redisRepository;

    //수량 검증
    public boolean availableTotalIssueQuantity(long couponId, Integer totalQuantity){
        if(totalQuantity == null){
            return true;
        }
        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    //중복발급 검증요청
    public boolean availableUserIssueQuantity(long couponId, long userId){
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }

}
