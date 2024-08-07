package com.lemontree.interview.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 에러 응답 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Getter
public class ErrorResponse {

    private final HttpStatus status;
    private final String message;
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(HttpStatus status, String message, Map<String, String> validation) {
        this.status = status;
        this.message = message;
        this.validation = validation != null ? validation : new ConcurrentHashMap<>();
    }

    public void addValidation(String field, String errorMessage) {
        validation.put(field, errorMessage);
    }

}
