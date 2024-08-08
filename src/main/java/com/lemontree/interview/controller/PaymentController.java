package com.lemontree.interview.controller;

import com.lemontree.interview.request.PaymentRequest;
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
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 생성 메서드입니다.
     *
     * @param memberId 회원 ID
     * @return 201 (CREATED)
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<Void> payment(@PathVariable("memberId") Long memberId,
                                        @Valid @RequestBody PaymentRequest request) {

        paymentService.payment(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
