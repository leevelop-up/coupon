package com.example.couponcore.repository.mariadb;

import com.example.couponcore.model.Coupon;
import com.example.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long>{
    
}
