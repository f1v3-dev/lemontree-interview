package com.lemontree.interview.exception.trade;

import com.lemontree.interview.exception.GeneralException;

/**
 * 거래를 찾을 수 없을 때 발생하는 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class TradeNotFoundException extends GeneralException {

    private static final String MESSAGE = "해당 거래가 존재하지 않습니다.";

    public TradeNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
