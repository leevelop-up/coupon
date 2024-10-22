package com.example.couponcore.model;

import com.example.couponcore.exception.CouponIssueException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name= "coupons")
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;


    //이슈 함수 조건 메서드(수량에 대한 검증)
    public boolean availableIssueQuantity(){
        if(totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;
    }
    //이슈 함수 조건 메서드(발급 기한에 대한 검증)
    public boolean availableIssueDate(){
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }
    public boolean isIssueComplete() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueEnd.isBefore(now) || !availableIssueQuantity();
    }

    //이슈가 발생되었을때 발급 수량을 1올려준다.(조건 필요)
    public void issue(){
        if(!availableIssueQuantity()){
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,"발급 가능한 수량을 초과합니다. total:%s, issued:%s".formatted(totalQuantity,issuedQuantity));
        }
        if(!availableIssueDate()){
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,"발급 가능한 기간이 아닙니다.request: %s, issueStart: %s, issueEnd: %s".formatted(LocalDateTime.now(),dateIssueStart,dateIssueEnd));
        }
        issuedQuantity++;
    }

}
