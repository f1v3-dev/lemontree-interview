package com.lemontree.interview.exception.payback;

import com.lemontree.interview.exception.GeneralException;

/**
 * 페이백 취소가 허용되지 않을 때 발생하는 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
public class PaybackCancelNotAllowedException extends GeneralException {

    private static final String MESSAGE = "페이백 취소가 허용되지 않습니다.";

    public PaybackCancelNotAllowedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
