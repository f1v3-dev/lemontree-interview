package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.member.BalanceLackException;
import com.lemontree.interview.exception.member.DailyLimitExceedException;
import com.lemontree.interview.exception.member.MonthlyLimitExceedException;
import com.lemontree.interview.exception.member.OnceLimitExceedException;
import com.lemontree.interview.exception.payment.PaymentNotCompleteException;
import com.lemontree.interview.exception.trade.TradeNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.TradeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 결제 서비스 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 12.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    PaymentService paymentService;

    @Mock
    PaybackService paybackService;

    @Mock
    TradeRepository tradeRepository;

    @Mock
    MemberRepository memberRepository;


    @Test
    @DisplayName("결제 요청 - 실패 (1회 결제 한도 초과)")
    void 결제요청_실패_1회한도초과() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(8_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // expected
        assertThrows(OnceLimitExceedException.class
                , () -> paymentService.processPayment(paymentId));

        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 요청 - 실패 (1일 결제 한도 초과)")
    void 결제요청_실패_1일한도초과() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "dailyAccumulate", BigDecimal.valueOf(8_000L));

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // expected
        assertThrows(DailyLimitExceedException.class
                , () -> paymentService.processPayment(paymentId));

        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 요청 - 실패 (1달 결제 한도 초과)")
    void 결제요청_실패_1달한도초과() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "monthlyAccumulate", BigDecimal.valueOf(28_000L));

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));


        // expected
        assertThrows(MonthlyLimitExceedException.class
                , () -> paymentService.processPayment(paymentId));

        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 요청 - 실패 (잔액 부족)")
    void 결제요청_실패_잔액부족() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(1_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // expected
        assertThrows(BalanceLackException.class
                , () -> paymentService.processPayment(paymentId));

        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 요청 - 성공")
    void 결제요청_성공() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.processPayment(paymentId);

        // then
        assertEquals(1L, paymentId);

        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, times(1)).findWithPessimisticLockById(paymentId);
    }

    @Test
    @DisplayName("결제 취소 - 실패 (존재하지 않는 결제 ID)")
    void 결제취소_실패_존재하지않는결제() {

        // given
        Long notExistsPaymentId = 1L;

        when(tradeRepository.findWithPessimisticLockById(notExistsPaymentId))
                .thenReturn(Optional.empty());

        // expected
        assertThrows(TradeNotFoundException.class,
                () -> paymentService.cancelPayment(notExistsPaymentId));

        verify(tradeRepository, times(1)).findWithPessimisticLockById(notExistsPaymentId);
    }

    @Test
    @DisplayName("결제 취소 - 실패 (결제 상태가 완료가 아닌 경우)")
    void 결제취소_실패_결제상태오류() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);
        ReflectionTestUtils.setField(payment, "paymentStatus", PaymentStatus.WAIT);

        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // expected
        assertThrows(PaymentNotCompleteException.class,
                () -> paymentService.cancelPayment(paymentId));

        verify(tradeRepository, times(1)).findWithPessimisticLockById(paymentId);
    }

    @Test
    @DisplayName("결제 취소 - 성공 (페이백 정보가 존재하는 경우)")
    void 결제취소_성공_페이백존재() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);
        ReflectionTestUtils.setField(payment, "paymentStatus", PaymentStatus.DONE);
        ReflectionTestUtils.setField(payment, "paymentApprovedAt", LocalDateTime.of(2024, 8, 12, 0, 0, 0));
        ReflectionTestUtils.setField(payment, "paybackStatus", PaybackStatus.DONE);
        ReflectionTestUtils.setField(payment, "paybackApprovedAt", LocalDateTime.of(2024, 8, 12, 0, 5, 0));


        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.cancelPayment(paymentId);

        // then
        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, times(1)).findWithPessimisticLockById(paymentId);
        verify(paybackService, times(1)).cancelPayback(any());
    }

    @Test
    @DisplayName("결제 취소 - 성공 (페이백 정보가 없는 경우)")
    void 결제취소_성공_페이백미진행() {

        // given
        Long memberId = 1L;

        Member member = Member.builder()
                .name("승조")
                .balance(BigDecimal.valueOf(10_000L))
                .balanceLimit(BigDecimal.valueOf(50_000L))
                .onceLimit(BigDecimal.valueOf(5_000L))
                .dailyLimit(BigDecimal.valueOf(10_000L))
                .monthlyLimit(BigDecimal.valueOf(30_000L))
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        Long paymentId = 1L;

        Trade payment = Trade.builder()
                .memberId(memberId)
                .paymentAmount(BigDecimal.valueOf(5_000L))
                .paybackAmount(BigDecimal.valueOf(1_000L))
                .build();

        ReflectionTestUtils.setField(payment, "id", paymentId);
        ReflectionTestUtils.setField(payment, "paymentStatus", PaymentStatus.DONE);
        ReflectionTestUtils.setField(payment, "paymentApprovedAt", LocalDateTime.of(2024, 8, 12, 0, 0, 0));
        ReflectionTestUtils.setField(payment, "paybackStatus", PaybackStatus.WAIT);


        when(memberRepository.findWithPessimisticLockById(memberId))
                .thenReturn(Optional.of(member));

        when(tradeRepository.findWithPessimisticLockById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.cancelPayment(paymentId);

        // then
        verify(memberRepository, times(1)).findWithPessimisticLockById(memberId);
        verify(tradeRepository, times(1)).findWithPessimisticLockById(paymentId);
        verify(paybackService, never()).cancelPayback(any());
    }
}