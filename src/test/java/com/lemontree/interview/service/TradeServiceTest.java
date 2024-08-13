package com.lemontree.interview.service;

import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.trade.TradeNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.TradeRepository;
import com.lemontree.interview.request.TradeRequest;
import com.lemontree.interview.response.TradeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 거래 서비스 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 13.
 */
@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @InjectMocks
    TradeService tradeService;

    @Mock
    TradeRepository tradeRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("거래 정보 조회 - 실패 (정보가 없는 경우)")
    void 거래정보조회_실패() {

        // given
        Long notExistTradeId = 1L;
        when(tradeRepository.findById(notExistTradeId)).thenReturn(Optional.empty());

        // expected
        assertThrows(TradeNotFoundException.class,
                () -> tradeService.getTrade((notExistTradeId)));

        verify(tradeRepository, times(1)).findById(notExistTradeId);
    }

    @Test
    @DisplayName("거래 정보 조회 - 성공")
    void 거래정보조회_성공() {


        // given
        Long tradeId = 1L;

        Trade trade = Trade.builder()
                .memberId(1L)
                .paymentAmount(BigDecimal.valueOf(10_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();
        ReflectionTestUtils.setField(trade, "id", tradeId);

        when(tradeRepository.findById(tradeId))
                .thenReturn(Optional.of(trade));

        // when
        TradeResponse actual = tradeService.getTrade(tradeId);

        // then
        assertAll(
                () -> assertEquals(tradeId, actual.getTradeId()),
                () -> assertEquals(1L, actual.getMemberId()),
                () -> assertEquals(BigDecimal.valueOf(10_000L), actual.getPaymentAmount()),
                () -> assertEquals(BigDecimal.valueOf(1_000L), actual.getPaybackAmount()),
                () -> assertEquals(PaymentStatus.WAIT, actual.getPaymentStatus()),
                () -> assertEquals(PaybackStatus.WAIT, actual.getPaybackStatus())
        );

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    @DisplayName("거래 생성 - 실패 (존재하지 않는 유저 ID)")
    void 거래_생성_실패_존재하지않는유저() {

        // given
        Long notExistsMemberId = 1L;

        when(memberRepository.existsById(notExistsMemberId)).thenReturn(false);

        TradeRequest request = new TradeRequest();

        // expected
        assertThrows(MemberNotFoundException.class,
                () -> tradeService.requestTrade(notExistsMemberId, request));
    }

    @Test
    @DisplayName("거래 생성 - 성공")
    void 거래_생성_성공() {

        // given
        Long memberId = 1L;

        when(memberRepository.existsById(memberId)).thenReturn(true);

        TradeRequest request = new TradeRequest();
        ReflectionTestUtils.setField(request, "paymentAmount", BigDecimal.valueOf(5_000L));
        ReflectionTestUtils.setField(request, "paybackAmount", BigDecimal.valueOf(1_000L));

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", 1L);

        when(tradeRepository.save(any())).thenReturn(payment);

        // when
        Long tradeId = tradeService.requestTrade(memberId, request);

        // then
        assertEquals(1L, tradeId);

        verify(memberRepository, times(1)).existsById(memberId);
        verify(tradeRepository, times(1)).save(any());
    }
}