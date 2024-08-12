package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.enums.PaybackStatus;
import com.lemontree.interview.enums.PaymentStatus;
import com.lemontree.interview.exception.member.*;
import com.lemontree.interview.exception.payment.PaymentAlreadyProceedException;
import com.lemontree.interview.exception.payment.PaymentNotCompleteException;
import com.lemontree.interview.exception.payment.PaymentNotFoundException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

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

    private final PaybackService paybackService;
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
     * 결제건을 생성합니다. (결제가 진행되는 것이 아닌, 진행해야되는 결제건을 생성합니다.)
     *
     * @param memberId 결제를 진행할 유저 ID
     * @param request  결제 요청 정보
     * @return 결제 ID
     */
    @Transactional
    public Long createPayment(Long memberId, PaymentRequest request) {

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        Payment payment = Payment.builder()
                .memberId(memberId)
                .paymentAmount(request.getPaymentAmount())
                .paybackAmount(request.getPaybackAmount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment.getId();
    }

    /**
     * 결제를 진행합니다. 이 때, 비관적 락을 사용하여 멤버 정보를 조회하고 결제를 진행합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void processPayment(Long paymentId) {

        // 비관적 락을 사용하여 결제 정보를 조회합니다. (결제 상태 및 결제 금액 변경을 막기 위함)
        Payment payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        // 비관적 락을 사용하여 멤버 정보를 조회합니다. (잔액 변경을 막기 위함)
        Member member = memberRepository.findWithPessimisticLockById(payment.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        if (payment.getPaymentStatus() != PaymentStatus.WAIT) {
            throw new PaymentAlreadyProceedException();
        }

        checkLimitAndBalance(member, payment.getPaymentAmount());
        member.pay(payment.getPaymentAmount());

        payment.completePayment();
        log.info("결제가 완료되었습니다. [결제 ID = {}]", payment.getId());
    }


    /**
     * 결제 취소를 진행합니다. 만약 페이백 정보가 존재한다면, 페이백도 동시에 취소합니다.
     *
     * @param paymentId 결제 ID
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void cancelPayment(Long paymentId) {

        // 비관적 락을 사용하여 결제 정보 조회 (결제 상태를 다른 트랜잭션에서 변경하지 못하도록)
        Payment payment = paymentRepository.findWithPessimisticLockById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        // 비관적 락을 사용하여 회원 정보 조회 (유저 잔액 수정을 막아야 함.)
        Member member = memberRepository.findWithPessimisticLockById(payment.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new PaymentNotCompleteException();
        }

        // 페이백도 진행되었을 경우 우선적으로 취소 진행
        if (payment.getPaybackStatus() == PaybackStatus.DONE) {
            // TODO: 여기서 오류가 발생한다고 하더라도 결제 취소는 진행되어야 합니다. (미구현)
            try {
                paybackService.cancelPayback(paymentId);
            } catch (Exception e) {
                log.error("페이백 취소 중 오류가 발생하였습니다. [결제 ID = {}]", paymentId, e);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        payment.cancelPayment(now);
        member.cancelPayment(payment.getPaymentAmount());

        // 1. 결제한 일자와 취소하는 일자(오늘)이 같은 날짜인가?
        LocalDateTime approvedAt = payment.getPaymentApprovedAt();
        if (compareDay(now, approvedAt) == 0) {
            BigDecimal paymentAmount = payment.getPaymentAmount();
            member.decreaseDailyAccumulate(paymentAmount);
        }

        // 2. 결제한 일자와 취소하는 일자(오늘)이 같은 달인가?
        if (compareMonth(now, approvedAt) == 0) {
            BigDecimal paymentAmount = payment.getPaymentAmount();
            member.decreaseMonthlyAccumulate(paymentAmount);
        }

        log.info("결제 취소가 완료되었습니다. [결제 ID = {}]", paymentId);
    }


    /**
     * 두 날짜의 일자를 비교합니다.
     *
     * @param aDateTime 날짜 A
     * @param bDateTime 날짜 B
     * @return 같은 경우 0, aDate가 더 큰 경우 1, bDate가 더 큰 경우 -1
     */
    private static int compareDay(LocalDateTime aDateTime, LocalDateTime bDateTime) {
        LocalDate aDate = aDateTime.toLocalDate();
        LocalDate bDate = bDateTime.toLocalDate();
        return aDate.compareTo(bDate);
    }

    /**
     * 두 날짜의 월을 비교합니다.
     *
     * @param aDateTime 날짜 A
     * @param bDateTime 날짜 B
     * @return 같은 경우 0, aDate가 더 큰 경우 1, bDate가 더 큰 경우 -1
     */
    private static int compareMonth(LocalDateTime aDateTime, LocalDateTime bDateTime) {
        YearMonth aYearMonth = YearMonth.from(aDateTime);
        YearMonth bYearMonth = YearMonth.from(bDateTime);
        return aYearMonth.compareTo(bYearMonth);
    }


    /**
     * 한도 초과 및 잔액 부족 체크
     *
     * @param member 결제를 진행한 회원
     * @param amount 결제 금액
     */
    private void checkLimitAndBalance(Member member, BigDecimal amount) {

        if (BigDecimalUtils.is(amount).greaterThan(member.getOnceLimit())) {
            throw new OnceLimitExceedException();
        }

        BigDecimal expectedDailyAccum = member.getDailyAccumulate().add(amount);
        if (BigDecimalUtils.is(expectedDailyAccum).greaterThan(member.getDailyLimit())) {
            throw new DailyLimitExceedException();
        }


        BigDecimal expectedMonthlyAccum = member.getMonthlyAccumulate().add(amount);
        if (BigDecimalUtils.is(expectedMonthlyAccum).greaterThan(member.getMonthlyLimit())) {
            throw new MonthlyLimitExceedException();
        }

        BigDecimal balance = member.getBalance();
        if (BigDecimalUtils.is(balance).lessThan(amount)) {
            throw new BalanceLackException();
        }

        // 결제 후 잔액이 음수가 되는지 체크
        BigDecimal expectedBalance = balance.subtract(amount);
        if (BigDecimalUtils.is(expectedBalance).lessThan(BigDecimal.ZERO)) {
            throw new BalanceLackException();
        }
    }


}
