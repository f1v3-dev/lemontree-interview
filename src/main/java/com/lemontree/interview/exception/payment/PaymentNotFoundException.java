package com.lemontree.interview.exception.payment;

import com.lemontree.interview.exception.GeneralException;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class PaymentNotFoundException extends GeneralException {

    private static final String MESSAGE = "결제를 찾을 수 없습니다.";

    public PaymentNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
