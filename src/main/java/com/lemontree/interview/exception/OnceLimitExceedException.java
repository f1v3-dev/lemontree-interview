package com.lemontree.interview.exception;

/**
 * 1회 한도 초과 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
public class OnceLimitExceedException extends GeneralException {

    private static final String MESSAGE = "1회 한도를 초과하였습니다.";

    public OnceLimitExceedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
