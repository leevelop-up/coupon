package com.example.couponcore.utill;

public class CouponRedisUtils {

    //redis key 생성
    public static String getIssueRequestKey(long couponId) {
        return "issue.request.sorted_set.couponId=%s".formatted(couponId);
    }
    public static String getIssueRequestQueueKey() {
        return "issue.request";
    }

}
