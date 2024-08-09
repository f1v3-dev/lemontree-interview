package com.lemontree.interview.exception.member;

import com.lemontree.interview.exception.GeneralException;

/**
 * 잔액 부족 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public class BalanceLackException extends GeneralException {

    private static final String MESSAGE = "잔액이 부족합니다.";

    public BalanceLackException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
