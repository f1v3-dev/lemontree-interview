package com.lemontree.interview.entity;

import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.payback.PaybackCancelNotAllowedException;
import com.lemontree.interview.exception.payment.PaymentCancelNotAllowedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 결제 Entity 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
@Entity
@Table(name = "payment",
        indexes = {
                @Index(name = "idx_payment_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false, name = "member_id")
    private Long memberId;

    @Column(nullable = false, name = "payment_amount")
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)", name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(nullable = false, name = "payback_amount")
    private BigDecimal paybackAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)", name = "payback_status")
    private PaybackStatus paybackStatus;

    @Builder
    public Payment(Long memberId, BigDecimal paymentAmount, BigDecimal paybackAmount) {
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
    }

    /**
     * 페이백이 정상적으로 완료되었을 때 페이백 상태를 완료(DONE)으로 변경합니다.
     */
    public void completePayback() {
        this.paybackStatus = PaybackStatus.DONE;
    }

    /**
     * 결제 취소 요청에 따른 결제 상태 변경
     */
    public void cancelPayment() {

        if (this.paymentStatus != PaymentStatus.DONE) {
            throw new PaymentCancelNotAllowedException();
        }

        this.paymentStatus = PaymentStatus.CANCEL;
    }

    /**
     * 페이백 취소 요청에 따른 페이백 상태 변경
     */
    public void cancelPayback() {

        if (this.paybackStatus != PaybackStatus.DONE) {
            throw new PaybackCancelNotAllowedException();
        }

        this.paybackStatus = PaybackStatus.CANCEL;
    }
}
