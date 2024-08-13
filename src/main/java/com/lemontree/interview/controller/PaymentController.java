package com.lemontree.interview.controller;

import com.lemontree.interview.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제 Controller 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제를 처리하는 메서드입니다.
     *
     * @param tradeId 처리할 결제 ID
     * @return 200 (OK)
     */
    @PostMapping("/api/v1/trades/{tradeId}/payments")
    public ResponseEntity<Void> processPayment(@PathVariable("tradeId") Long tradeId) {
        paymentService.processPayment(tradeId);
        return ResponseEntity.ok().build();
    }


    /**
     * 결제 취소 메서드입니다.
     *
     * @param tradeId 결제 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/api/v1/trades/{tradeId}/payments")
    public ResponseEntity<Void> cancelPayment(@PathVariable("tradeId") Long tradeId) {
        paymentService.cancelPayment(tradeId);
        return ResponseEntity.ok().build();
    }
}
