package com.example.couponapi.service;

import com.example.couponapi.controller.dto.CouponIssueRequestDto;
import com.example.couponcore.component.DistributeLockExecutor;
import com.example.couponcore.service.AsyncCouponIssueServiceV1;
import com.example.couponcore.service.AsyncCouponIssueServiceV2;
import com.example.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;
    private final Logger logger = Logger.getLogger(CouponIssueRequestService.class.getName());

    public void issueRequestV1(CouponIssueRequestDto requestDto){
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());

//        //redis 를 사용하여 동기화 처리
//        distributeLockExecutor.execute("lock_"+requestDto.couponId(),10000,10000,()->{
//            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
//        });

        //synchronized 블록으로 동기화 처리
//        synchronized (this) {
//            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
//        }
        logger.info("쿠폰 발급 완료,couponId: s, userId: s".formatted(requestDto.couponId(), requestDto.userId()));
    }

    public void asyncIssueRequestV1(CouponIssueRequestDto requestDto){
        asyncCouponIssueServiceV1.issue(requestDto.couponId(), requestDto.userId());
    }

    public void asyncIssueRequestV2(CouponIssueRequestDto requestDto){
        asyncCouponIssueServiceV1.issue(requestDto.couponId(), requestDto.userId());
    }
}
