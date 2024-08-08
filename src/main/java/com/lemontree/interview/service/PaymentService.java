package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.entity.Payment;
import com.lemontree.interview.exception.DailyLimitExceedException;
import com.lemontree.interview.exception.MemberNotFoundException;
import com.lemontree.interview.exception.MonthlyLimitExceedException;
import com.lemontree.interview.exception.OnceLimitExceedException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.repository.PaymentRepository;
import com.lemontree.interview.request.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
     * 결제 요청을 한 유저 정보를 조회하고 요구사항에 맞게 결제를 진행합니다.
     *
     * @param memberId 결제를 진행할 유저의 ID
     * @param request  결제 요청 정보 (결제 금액)
     */
    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public void payment(Long memberId, PaymentRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 한도 초과 및 잔액 부족 체크
        checkLimitAndBalance(member, request.getAmount());

        // TODO: 페이백 금액은 결제를 할 때 알려주는건가? 아니면, 고정된 금액이 존재? 아니면, 결제 금액의 일정 비율로 지급?
        Payment payment = Payment.builder()
                .member(member)
                .paymentAmount(request.getAmount())
                .build();

        paymentRepository.save(payment);

        // TODO: 결제 후 페이백 지급 API 추가

    }


    /**
     * 한도 초과 및 잔액 부족 체크
     *
     * @param member 결제를 진행한 회원
     * @param amount 결제 금액
     */
    private void checkLimitAndBalance(Member member, Long amount) {

        // TODO: 예외 처리 클래스 만들어서 처리하는게 좋을듯 ?

        // 1회 한도
        if (member.getOnceLimit() < amount) {
            throw new OnceLimitExceedException();
        }

        // 일일 한도
        if (member.getDailyLimit() < member.getDailyAccumulate() + amount) {
            throw new DailyLimitExceedException();
        }

        // 월간 한도
        if (member.getMonthlyLimit() < member.getMonthlyAccumulate() + amount) {
            throw new MonthlyLimitExceedException();
        }

        // 잔액 부족 체크
        if (member.getBalance() < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 위의 예외 사항을 모두 만족하였을 경우, 누적 금액(일간, 월간)과 잔액을 갱신합니다.
        member.updateAccumulateAndBalance(amount);
    }

}
