package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.exception.member.*;
import com.lemontree.interview.exception.payment.PaymentNotFoundException;
import com.lemontree.interview.exception.payment.PaymentUnauthorizedException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.PaymentRepository;
import com.lemontree.interview.request.PaymentRequest;
import com.lemontree.interview.response.PaymentResponse;
import com.lemontree.interview.util.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 결제 Service 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    /**
     * 결제 정보를 조회합니다.
     *
     * @param paymentId 결제 ID
     * @return 결제 응답 정보
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        return new PaymentResponse(payment);
    }


    /**
     * 결제 요청을 한 유저 정보를 조회하고 요구사항에 맞게 결제를 진행합니다.
     *
     * @param memberId 결제를 진행할 유저의 ID
     * @param request  결제 요청 정보 (결제 금액)
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void processPayment(Long memberId, PaymentRequest request) {

        // 비관적 락을 사용하여 멤버 정보를 조회합니다.
        Member member = memberRepository.findWithPessimisticLockById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 한도 초과 및 잔액 부족 체크
        checkLimitAndBalance(member, request.getPaymentAmount());

        Payment payment = Payment.builder()
                .memberId(memberId)
                .paymentAmount(request.getPaymentAmount())
                .paybackAmount(request.getPaybackAmount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // -> 여기서 예외가 발생하면 rollback?

        // 결제 완료 상태로 변경
        savedPayment.completePayment();

        log.info("결제가 완료되었습니다. [결제 ID = {}]", savedPayment.getId());
    }


    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void cancelPayment(Long memberId, Long paymentId) {

        // 비관적 락을 사용하여 회원 정보 조회
        Member member = memberRepository.findWithPessimisticLockById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 비관적 락을 사용하여 결제 정보 조회
        Payment payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);


        // 결제를 한 유저와 결제 취소를 요청한 유저가 같은 유저인지 체크합니다.
        if (!payment.getMemberId().equals(member.getId())) {
            throw new PaymentUnauthorizedException();
        }

        // 1. 결제 취소 로직
        payment.cancelPayment();
        member.refund(payment.getPaymentAmount());
    }


    /**
     * 한도 초과 및 잔액 부족 체크
     *
     * @param member 결제를 진행한 회원
     * @param amount 결제 금액
     */
    private void checkLimitAndBalance(Member member, BigDecimal amount) {

        // 1회 한도
        if (BigDecimalUtils.is(amount).greaterThan(member.getOnceLimit())) {
            throw new OnceLimitExceedException();
        }

        // 일일 한도
        BigDecimal expectedDailyAccum = member.getDailyAccumulate().add(amount);
        if (BigDecimalUtils.is(expectedDailyAccum).greaterThan(member.getDailyLimit())) {
            throw new DailyLimitExceedException();
        }


        // 월간 한도
        BigDecimal expectedMonthlyAccum = member.getMonthlyAccumulate().add(amount);
        if (BigDecimalUtils.is(expectedMonthlyAccum).greaterThan(member.getMonthlyLimit())) {
            throw new MonthlyLimitExceedException();
        }

        // 잔액 부족 체크
        if (BigDecimalUtils.is(member.getBalance()).lessThan(amount)) {
            throw new BalanceLackException();
        }

        // 위의 예외 사항을 모두 만족하였을 경우, 누적 금액(일간, 월간)과 잔액을 갱신합니다.
        member.updateAccumulateAndBalance(amount);
    }

}
