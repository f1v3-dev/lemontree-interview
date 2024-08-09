package com.lemontree.interview.exception.payment;

import com.lemontree.interview.exception.GeneralException;

/**
 * 결제 취소가 허용되지 않을 때 발생하는 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class PaymentCancelNotAllowedException extends GeneralException {

    private static final String MESSAGE = "결제 취소가 허용되지 않습니다.";

    public PaymentCancelNotAllowedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
