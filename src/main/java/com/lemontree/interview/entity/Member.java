package com.lemontree.interview.entity;

import com.lemontree.interview.exception.payment.PaymentCancelNotAllowedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

/**
 * 유저 Entity 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE member_id = ?")
@SQLRestriction("is_deleted = FALSE") // 삭제되지 않은 데이터만 조회
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "balance")
    private BigDecimal balance;

    @Column(nullable = false, name = "once_limit")
    private BigDecimal onceLimit;

    @Column(nullable = false, name = "daily_limit")
    private BigDecimal dailyLimit;

    @Column(nullable = false, name = "monthly_limit")
    private BigDecimal monthlyLimit;

    @Column(nullable = false, name = "daily_accumulate")
    private BigDecimal dailyAccumulate;

    @Column(nullable = false, name = "monthly_accumulate")
    private BigDecimal monthlyAccumulate;

    @Column(nullable = false, name = "is_deleted")
    private Boolean isDeleted;

    /**
     * 유저 생성자입니다. (Builder Pattern)
     *
     * @param name         유저 이름
     * @param balance      유저의 보유 금액
     * @param onceLimit    유저가 한 번에 사용할 수 있는 금액
     * @param dailyLimit   유저가 하루에 사용할 수 있는 금액
     * @param monthlyLimit 유저가 한 달에 사용할 수 있는 금액
     */
    @Builder
    public Member(String name, BigDecimal balance, BigDecimal onceLimit, BigDecimal dailyLimit, BigDecimal monthlyLimit,
                  Boolean isDeleted) {
        this.name = name;
        this.balance = balance;
        this.onceLimit = onceLimit;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.dailyAccumulate = BigDecimal.ZERO;
        this.monthlyAccumulate = BigDecimal.ZERO;
        this.isDeleted = isDeleted != null ? isDeleted : Boolean.FALSE;
    }


    /**
     * 유저의 누적 금액과 잔액을 업데이트합니다.
     *
     * @param amount 결제 금액
     */
    public void updateAccumulateAndBalance(BigDecimal amount) {
        updateDailyAccumulate(amount);
        updateMonthlyAccumulate(amount);
        updateBalance(amount);
    }

    /**
     * 유저의 잔액을 업데이트합니다.
     *
     * @param amount 차감 금액
     */
    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    /**
     * 유저의 일간 누적 금액을 업데이트합니다.
     *
     * @param amount 결제 금액
     */
    public void updateDailyAccumulate(BigDecimal amount) {
        this.dailyAccumulate = this.dailyAccumulate.add(amount);
    }

    /**
     * 유저의 월간 누적 금액을 업데이트합니다.
     *
     * @param amount 결제 금액
     */
    public void updateMonthlyAccumulate(BigDecimal amount) {
        this.monthlyAccumulate = this.monthlyAccumulate.add(amount);
    }

    /**
     * 유저에게 페이백 금액을 지급합니다.
     *
     * @param paybackAmount 페이백 금액
     */
    public void payback(BigDecimal paybackAmount) {
        this.balance = this.balance.add(paybackAmount);
    }

    /**
     * 결제 취소를 진행합니다.
     *
     * @param paymentAmount 결제 금액
     */
    public void refund(BigDecimal paymentAmount) {
        this.balance = this.balance.add(paymentAmount);
    }

    /**
     * 페이백 취소를 진행합니다.
     *
     * @param paybackAmount 페이백 금액
     */
    public void revokePayback(BigDecimal paybackAmount) {

        // 페이백 금액이 잔액보다 많은 경우, 결제 취소가 불가능합니다.
        if (this.balance.compareTo(paybackAmount) < 0) {
            throw new PaymentCancelNotAllowedException();
        }

        this.balance = this.balance.subtract(paybackAmount);
    }
}
