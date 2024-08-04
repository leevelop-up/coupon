package com.example.couponcore.repository.mariadb;

import com.example.couponcore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long>{

}
