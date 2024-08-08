package com.lemontree.interview.exception;

/**
 * 유저를 찾을 수 없을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public class MemberNotFoundException extends GeneralException {

    private static final String MESSAGE = "해당 유저를 찾을 수 없습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
