package com.lemontree.interview.service;

import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.trade.TradeNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.TradeRepository;
import com.lemontree.interview.request.TradeRequest;
import com.lemontree.interview.response.TradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 거래 Service 클래스 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 13.
 */
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;

    /**
     * 거래를 생성합니다. (결제가 진행되는 것이 아닌, 진행해야되는 거래를 생성합니다.)
     *
     * @param memberId 거래를 생성할 유저 ID
     * @param request  거래 요청 정보
     * @return 생성된 거래 ID
     */
    @Transactional
    public Long requestTrade(Long memberId, TradeRequest request) {

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        Trade trade = Trade.builder()
                .memberId(memberId)
                .paymentAmount(request.getPaymentAmount())
                .paybackAmount(request.getPaybackAmount())
                .build();

        Trade savedTrade = tradeRepository.save(trade);

        return savedTrade.getId();
    }

    /**
     * 거래 조회 메서드입니다.
     *
     * @param tradeId 거래 ID
     * @return 결제 응답 DTO
     */
    @Transactional(readOnly = true)
    public TradeResponse getTrade(Long tradeId) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(TradeNotFoundException::new);

        return new TradeResponse(trade);
    }
}
