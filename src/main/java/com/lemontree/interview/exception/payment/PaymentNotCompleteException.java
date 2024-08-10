package com.lemontree.interview.exception.payment;

import com.lemontree.interview.exception.GeneralException;

/**
 * 결제가 완료되지 않았을 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
public class PaymentNotCompleteException extends GeneralException {

    private static final String MESSAGE = "결제가 완료되지 않았습니다.";

    public PaymentNotCompleteException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
