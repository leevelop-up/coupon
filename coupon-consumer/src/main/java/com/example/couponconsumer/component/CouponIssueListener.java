package com.example.couponconsumer.component;

import com.example.couponcore.repository.redis.RedisRepository;
import com.example.couponcore.repository.redis.dto.CouponIssueRequest;
import com.example.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.example.couponcore.utill.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@EnableScheduling
@Component
public class CouponIssueListener {

    private final CouponIssueService couponIssueService;
    private final RedisRepository redisRepository;
    private final String issueRequestQueueKey = getIssueRequestQueueKey();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(CouponIssueListener.class);
    @Scheduled(fixedDelay = 1000L)
    public void issue() throws JsonProcessingException {
        log.info("issue start");
        while(existCouponIssueTarget()){
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작: %s".formatted(target));
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 끝: %s".formatted(target));
            removeIssuedTarget();
        }
    }

    private boolean existCouponIssueTarget(){

        return redisRepository.lSize(issueRequestQueueKey) > 0;
    }
    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {

        return objectMapper.readValue(redisRepository.lIndex(issueRequestQueueKey, 0), CouponIssueRequest.class);
    }

    private void removeIssuedTarget(){
        redisRepository.lPop(issueRequestQueueKey);
    }
}
