package com.example.couponapi.service;

import com.example.couponapi.controller.dto.CouponIssueRequestDto;
import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final Logger logger = Logger.getLogger(CouponIssueRequestService.class.getName());

    public void issueRequestV1(CouponIssueRequestDto requestDto){
        //redis 를 사용하여 동기화 처리
        distributeLockExecutor.execute("lock_"+requestDto.couponId(),10000,10000,()->{
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        });

        //synchronized 블록으로 동기화 처리
//        synchronized (this) {
//            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
//        }
        logger.info("쿠폰 발급 완료,couponId: s, userId: s".formatted(requestDto.couponId(), requestDto.userId()));
    }
}
