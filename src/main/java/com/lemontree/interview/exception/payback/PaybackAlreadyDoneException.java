package com.lemontree.interview.exception.payback;

import com.lemontree.interview.exception.GeneralException;

/**
 * 중복 페이백 요청일 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class PaybackAlreadyDoneException extends GeneralException {

    private static final String MESSAGE = "이미 페이백이 완료된 결제입니다.";

    public PaybackAlreadyDoneException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
