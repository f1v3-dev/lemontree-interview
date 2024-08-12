package com.lemontree.interview.exception.member;

import com.lemontree.interview.exception.GeneralException;

/**
 * 1일 한도가 1달 한도를 초과하는 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 12.
 */
public class DailyLimitExceedsMonthlyLimitException extends GeneralException {

    private static final String MESSAGE = "1일 한도가 1달 한도보다 클 수 없습니다.";

    public DailyLimitExceedsMonthlyLimitException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
