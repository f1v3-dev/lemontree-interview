package com.lemontree.interview.controller;

import com.lemontree.interview.request.PaymentRequest;
import com.lemontree.interview.response.PaymentResponse;
import com.lemontree.interview.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * 회원의 결제 요청 메서드입니다.
     *
     * @param memberId 회원 ID
     * @return 201 (CREATED)
     */
    @PostMapping("/api/v1/members/{memberId}/payments")
    public ResponseEntity<Void> requestPayment(@PathVariable("memberId") Long memberId,
                                               @Valid @RequestBody PaymentRequest request) {

        paymentService.processPayment(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 결제 취소 메서드입니다.
     *
     * @param memberId  요청 회원 ID
     * @param paymentId 결제 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/api/v1/members/{memberId}/payments/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable("memberId") Long memberId,
                                              @PathVariable("paymentId") Long paymentId) {
        paymentService.cancelPayment(memberId, paymentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 결제 조회 메서드입니다.
     *
     * @param paymentId 결제 ID
     * @return 200 (OK), body: 결제 응답 DTO
     */
    @GetMapping("/api/v1/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable("paymentId") Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }
}
