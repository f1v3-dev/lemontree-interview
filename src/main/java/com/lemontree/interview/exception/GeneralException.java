package com.lemontree.interview.exception;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 최상위 예외 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
public abstract class GeneralException extends RuntimeException {

    private final Map<String, String> validation = new ConcurrentHashMap<>();

    protected GeneralException(String message) {
        super(message);
    }

    public abstract int getStatusCode();

    public void addValidation(String field, String errorMessage) {
        validation.put(field, errorMessage);
    }

}
