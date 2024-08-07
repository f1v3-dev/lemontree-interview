package com.lemontree.interview.controller;

import com.lemontree.interview.exception.GeneralException;
import com.lemontree.interview.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 전역 예외 처리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@RestControllerAdvice
public class ErrorController {

    /**
     * Request DTO Validation 에러 처리 (BindingResult - MethodArgumentNotValidException)
     *
     * @param e MethodArgumentNotValidException
     * @return status: 400 (BAD_REQUEST), body: Validation Error Message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(MethodArgumentNotValidException e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("잘못된 요청입니다.")
                .build();

        List<FieldError> fieldErrors = e.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            body.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 전역 예외 처리 (GeneralException)
     *
     * @param e GeneralException
     * @return status: , body: e.getMessage(), e.getValidation()
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(GeneralException e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.valueOf(e.getStatusCode()))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(body);
    }

}
