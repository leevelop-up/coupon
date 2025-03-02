package com.example.couponconsumer.component;

import com.example.couponconsumer.TestConfig;
import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    CouponIssueListener sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisRepository redisRepository;

    @MockBean
    CouponIssueService couponIssueService;

    @BeforeEach
    void clear(){
        Collection<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("발급 큐에 대상이 없으면 발급 처리를 하지 않는다.")
    void issue_1() throws JsonProcessingException {
        sut.issue();
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("발급 큐에 대상이 있으면 발급 처리한다.")
    void issue_2() throws JsonProcessingException {
        long couponId = 1;
        long userId = 1;
        int totalIssueQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
        sut.issue();
        verify(couponIssueService, times(1)).issue(anyLong(), anyLong());
    }
    @Test
    @DisplayName("발급 요청 순서에 맞게 처리된다.")
    void issue_3() throws JsonProcessingException {
        long couponId = 1;
        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;

        int totalIssueQuantity = Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId1, totalIssueQuantity);
        redisRepository.issueRequest(couponId, userId2, totalIssueQuantity);
        redisRepository.issueRequest(couponId, userId3, totalIssueQuantity);
        sut.issue();
        InOrder inOrder = Mockito.inOrder(couponIssueService);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId2);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId3);
    }
}