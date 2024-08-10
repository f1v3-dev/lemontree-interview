package com.lemontree.interview.exception.payback;

import com.lemontree.interview.exception.GeneralException;

/**
 * 페이백이 완료되지 않았을 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
public class PaybackNotCompleteException extends GeneralException {

    private static final String MESSAGE = "페이백이 완료되지 않았습니다.";

    public PaybackNotCompleteException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
