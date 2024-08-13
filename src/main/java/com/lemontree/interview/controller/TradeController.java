package com.lemontree.interview.controller;

import com.lemontree.interview.request.TradeRequest;
import com.lemontree.interview.response.TradeResponse;
import com.lemontree.interview.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 거래 Controller 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 13.
 */
@RestController
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * 회원의 거래 생성 요청 메서드입니다.
     *
     * @param memberId 회원 ID
     * @return 201 (CREATED), body: 생성된 거래 ID
     */
    @PostMapping("/api/v1/members/{memberId}/trades")
    public ResponseEntity<Map<String, Long>> requestTrade(@PathVariable("memberId") Long memberId,
                                                          @Valid @RequestBody TradeRequest request) {

        Long tradeId = tradeService.requestTrade(memberId, request);
        Map<String, Long> response = Map.of("tradeId", tradeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 조회 메서드입니다.
     *
     * @param tadeId 결제 ID
     * @return 200 (OK), body: 결제 응답 DTO
     */
    @GetMapping("/api/v1/trades/{tradeId}")
    public ResponseEntity<TradeResponse> getTrade(@PathVariable("tradeId") Long tadeId) {
        return ResponseEntity.ok(tradeService.getTrade(tadeId));
    }
}
