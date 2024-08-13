package com.lemontree.interview.response;

import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 거래 응답 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
@Getter
public class TradeResponse {

    private final Long tradeId;
    private final Long memberId;
    private final BigDecimal paymentAmount;
    private final PaymentStatus paymentStatus;
    private final BigDecimal paybackAmount;
    private final PaybackStatus paybackStatus;

    public TradeResponse(final Trade trade) {
        this.tradeId = trade.getId();
        this.memberId = trade.getMemberId();
        this.paymentAmount = trade.getPaymentAmount();
        this.paymentStatus = trade.getPaymentStatus();
        this.paybackAmount = trade.getPaybackAmount();
        this.paybackStatus = trade.getPaybackStatus();
    }

}
