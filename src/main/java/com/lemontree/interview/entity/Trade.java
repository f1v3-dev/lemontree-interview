package com.lemontree.interview.entity;

import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래 Entity 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@Entity
@Table(name = "trade",
        indexes = {
                @Index(name = "idx_payment_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @Column(nullable = false, name = "member_id")
    private Long memberId;

    @Column(nullable = false, name = "payment_amount")
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_status", columnDefinition = "VARCHAR(10)")
    private PaymentStatus paymentStatus;

    @Column(nullable = false, name = "payback_amount")
    private BigDecimal paybackAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payback_status", columnDefinition = "VARCHAR(10)")
    private PaybackStatus paybackStatus;

    @Column(nullable = true, name = "payment_approved_at")
    private LocalDateTime paymentApprovedAt;

    @Column(nullable = true, name = "payment_canceled_at")
    private LocalDateTime paymentCanceledAt;

    @Column(nullable = true, name = "payback_approved_at")
    private LocalDateTime paybackApprovedAt;

    @Column(nullable = true, name = "payback_canceled_at")
    private LocalDateTime paybackCanceledAt;

    @Builder
    public Trade(Long memberId, BigDecimal paymentAmount, BigDecimal paybackAmount) {
        this.memberId = memberId;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = PaymentStatus.WAIT;
        this.paybackAmount = paybackAmount;
        this.paybackStatus = PaybackStatus.WAIT;
    }

    /**
     * 결제가 정상적으로 완료되었을 때 결제 상태를 완료(DONE)으로 변경합니다.
     */
    public void completePayment() {
        this.paymentStatus = PaymentStatus.DONE;
        this.paymentApprovedAt = LocalDateTime.now();
    }


    /**
     * 결제 취소 요청에 따른 결제 상태 변경
     */
    public void cancelPayment(LocalDateTime now) {
        this.paymentStatus = PaymentStatus.CANCEL;
        this.paymentCanceledAt = now;
    }

    /**
     * 페이백이 정상적으로 완료되었을 때 페이백 상태를 완료(DONE)으로 변경합니다.
     */
    public void completePayback() {
        this.paybackStatus = PaybackStatus.DONE;
        this.paybackApprovedAt = LocalDateTime.now();
    }

    /**
     * 페이백 취소 요청에 따른 페이백 상태 변경
     */
    public void cancelPayback() {
        this.paybackStatus = PaybackStatus.CANCEL;
        this.paybackCanceledAt = LocalDateTime.now();
    }
}
