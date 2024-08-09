package com.lemontree.interview.exception.payment;

import com.lemontree.interview.exception.GeneralException;

/**
 * 결제에 대한 권한이 없을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class PaymentUnauthorizedException extends GeneralException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public PaymentUnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
