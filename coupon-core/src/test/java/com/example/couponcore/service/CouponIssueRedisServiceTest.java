package com.example.couponcore.service;

import com.example.couponcore.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.example.couponcore.utill.CouponRedisUtils.getIssueRequestKey;
import static org.junit.jupiter.api.Assertions.*;

class CouponIssueRedisServiceTest extends TestConfig {


    @Autowired
    CouponIssueRedisService sut;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear(){
        Collection<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("쿠폰 발급 가능 수량 검증 성공시 true")
    void availableTotalIssueQuantity_true(){
        int totalQuantity = 10;
        long couponId = 1;

        boolean result = sut.availableTotalIssueQuantity(couponId, totalQuantity);
        Assertions.assertTrue(result);
    }
    @Test
    @DisplayName("쿠폰 발급 가능 수량 검증 소진시 false")
    void availableTotalIssueQuantity_false(){
        int totalQuantity = 10;
        long couponId = 1;
        IntStream.range(0, totalQuantity).forEach(i -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(i));
        });

        boolean result = sut.availableTotalIssueQuantity(couponId, totalQuantity);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("중복발급 검증 -발급 내역에 유저가 존재하지 않으면 true")
    void availableUserIssueQuantity_true(){
        long userId = 1;
        long couponId = 1;
        boolean result = sut.availableUserIssueQuantity(couponId, userId);
        Assertions.assertTrue(result);
    }
    @Test
    @DisplayName("중복발급 검증 -발급 내역에 유저가 존재하면 false")
    void availableUserIssueQuantity_false(){
        long userId = 1;
        long couponId = 1;
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        boolean result = sut.availableUserIssueQuantity(couponId, userId);
        Assertions.assertFalse(result);
    }
}