package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.exception.payback.PaybackAlreadyDoneException;
import com.lemontree.interview.exception.payback.PaybackNotCompleteException;
import com.lemontree.interview.exception.payment.PaymentNotCompleteException;
import com.lemontree.interview.exception.payment.PaymentNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.PaymentRepository;
import com.lemontree.interview.util.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 페이백 Service 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 10.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaybackService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    /**
     * 페이백 처리를 진행합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void processPayback(Long paymentId) {

        Payment payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

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

            member.payback(paybackAmount);
        }

        payment.completePayback();
    }

    /**
     * 페이백 취소를 진행합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void cancelPayback(Long paymentId) {

        Payment payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new PaymentNotCompleteException();
        }

        if (payment.getPaybackStatus() != PaybackStatus.DONE) {
            throw new PaybackNotCompleteException();
        }

        BigDecimal paymentAmount = payment.getPaymentAmount();
        if (BigDecimalUtils.is(paymentAmount).greaterThan(BigDecimal.ZERO)) {
            Long memberId = payment.getMemberId();
            Member member = memberRepository.findWithPessimisticLockById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            member.revokePayback(paymentAmount);
        }

        payment.cancelPayback();

        log.info("페이백 취소가 완료되었습니다. [결제 ID = {}]", payment.getId());
    }
}
