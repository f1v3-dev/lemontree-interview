package com.lemontree.interview.exception.payment;

import com.lemontree.interview.exception.GeneralException;

/**
 * 중복 결제 요청시 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 13.
 */
public class PaymentAlreadyDoneException extends GeneralException {

    private static final String MESSAGE = "이미 결제가 진행된 결제건입니다.";

    public PaymentAlreadyDoneException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
