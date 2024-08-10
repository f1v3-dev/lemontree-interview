package com.lemontree.interview.controller;

import com.lemontree.interview.service.PaybackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 페이백 Controller 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
@RestController
@RequiredArgsConstructor
public class PaybackController {

    private final PaybackService paybackService;

    /**
     * 완료된 결제건에 대해 페이백을 요청하는 메서드입니다.
     *
     * @param paymentId 결제 ID
     * @return 201 (CREATED)
     */
    @PostMapping("/api/v1/payments/{paymentId}/payback")
    public ResponseEntity<Void> requestPayback(@PathVariable("paymentId") Long paymentId) {

        paybackService.processPayback(paymentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 완료된 페이백건에 대해 페이백 취소를 요청하는 메서드입니다.
     *
     * @param paymentId 결제 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/api/v1/payments/{paymentId}/payback")
    public ResponseEntity<Void> cancelPayback(@PathVariable("paymentId") Long paymentId) {

        paybackService.cancelPayback(paymentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
