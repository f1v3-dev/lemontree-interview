package com.lemontree.interview.response;

import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 결제 응답 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
@Getter
public class PaymentResponse {

    private final Long paymentId;
    private final Long memberId;
    private final BigDecimal paymentAmount;
    private final PaymentStatus paymentStatus;
    private final BigDecimal paybackAmount;
    private final PaybackStatus paybackStatus;

    public PaymentResponse(final Payment payment) {
        this.paymentId = payment.getId();
        this.memberId = payment.getMember().getId();
        this.paymentAmount = payment.getPaymentAmount();
        this.paymentStatus = payment.getPaymentStatus();
        this.paybackAmount = payment.getPaybackAmount();
        this.paybackStatus = payment.getPaybackStatus();
    }

}
