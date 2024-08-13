package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Trade;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.payback.PaybackAlreadyDoneException;
import com.lemontree.interview.exception.payback.PaybackCancelNotAllowedException;
import com.lemontree.interview.exception.payback.PaybackNotCompleteException;
import com.lemontree.interview.exception.payment.PaymentNotCompleteException;
import com.lemontree.interview.exception.trade.TradeNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.TradeRepository;
import com.lemontree.interview.util.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 페이백 Service 클래스 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaybackService {

    private final TradeRepository paymentRepository;
    private final MemberRepository memberRepository;

    /**
     * 페이백 처리를 진행합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void processPayback(Long paymentId) {

        Trade payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(TradeNotFoundException::new);

        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new PaymentNotCompleteException();
        }

        if (payment.getPaybackStatus() == PaybackStatus.DONE) {
            throw new PaybackAlreadyDoneException();
        }

        BigDecimal paybackAmount = payment.getPaybackAmount();
        if (BigDecimalUtils.is(paybackAmount).greaterThan(BigDecimal.ZERO)) {
            Long memberId = payment.getMemberId();
            Member member = memberRepository.findWithPessimisticLockById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            // 페이백 후 잔액이 한도를 초과하면 페이백이 불가능합니다.
            BigDecimal addedPayback = member.getBalance().add(paybackAmount);
            if (BigDecimalUtils.is(addedPayback).greaterThan(member.getBalanceLimit())) {
                throw new PaybackCancelNotAllowedException();
            }

            member.payback(paybackAmount);
        }

        payment.completePayback();

        log.info("페이백이 완료되었습니다. [결제 ID = {}]", payment.getId());
    }

    /**
     * 페이백 취소를 진행합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void cancelPayback(Long paymentId) {

        Trade payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(TradeNotFoundException::new);

        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new PaymentNotCompleteException();
        }

        if (payment.getPaybackStatus() != PaybackStatus.DONE) {
            throw new PaybackNotCompleteException();
        }

        BigDecimal paybackAmount = payment.getPaybackAmount();
        if (BigDecimalUtils.is(paybackAmount).greaterThan(BigDecimal.ZERO)) {
            Long memberId = payment.getMemberId();
            Member member = memberRepository.findWithPessimisticLockById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            // 페이백 금액을 회수해야 하는데 회원이 보유한 금액이 부족하면 페이백 취소가 불가능합니다.
            if (BigDecimalUtils.is(member.getBalance()).lessThan(paybackAmount)) {
                throw new PaybackCancelNotAllowedException();
            }

            member.cancelPayback(paybackAmount);
        }

        payment.cancelPayback();

        log.info("페이백 취소가 완료되었습니다. [결제 ID = {}]", payment.getId());

        // throw new RuntimeException("상위 트랜잭션이 roll-back 되는 문제가 존재함");
    }
}
