package com.lemontree.interview.exception.member;

import com.lemontree.interview.exception.GeneralException;

/**
 * 최대 보유 금액을 넘어선 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 11.
 */
public class BalanceExceededException extends GeneralException {

    private static final String MESSAGE = "최대 보유 금액을 넘어섰습니다.";

    public BalanceExceededException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
