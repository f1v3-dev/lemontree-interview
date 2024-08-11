package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
import com.lemontree.interview.util.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 유저 Service 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 모든 유저의 일일 누적 금액(daily_accumulate)을 0으로 초기화합니다.
     */
    @Transactional
    public void resetDailyLimit() {
        memberRepository.resetDailyLimit();
    }

    /**
     * 모든 유저의 월간 누적 금액(monthly_accumulate)을 0으로 초기화합니다.
     */
    @Transactional
    public void resetMonthlyLimit() {
        memberRepository.resetMonthlyLimit();
    }


    /**
     * 유저를 생성합니다.
     *
     * @param request 유저 생성 요청 DTO
     */
    @Transactional
    public Long createMember(MemberCreate request) {

        checkValidation(request);

        Member member = Member.builder()
                .name(request.getName())
                .balance(request.getBalance())
                .balanceLimit(request.getBalanceLimit())
                .onceLimit(request.getOnceLimit())
                .dailyLimit(request.getDailyLimit())
                .monthlyLimit(request.getMonthlyLimit())
                .isDeleted(request.getIsDeleted())
                .build();

        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    /**
     * 유저의 ID로 유저를 조회합니다.
     *
     * @param memberId 조회할 유저 ID
     * @return 유저 응답 DTO
     */
    @Transactional(readOnly = true)
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return new MemberResponse(member);
    }

    /**
     * 유저를 생성하기 전 사전 조건을 검증합니다.
     *
     * @param request 유저 생성 요청 DTO
     */
    private void checkValidation(MemberCreate request) {
        validateBalance(request.getBalance(), request.getBalanceLimit());
        validateLimit(request.getOnceLimit(), request.getDailyLimit(), request.getMonthlyLimit());
    }

    /**
     * 유저의 보유 금액과 최대 보유 금액을 검증합니다.
     *
     * @param balance      유저 보유 금액
     * @param balanceLimit 유저 최대 보유 금액
     */
    private void validateBalance(BigDecimal balance, BigDecimal balanceLimit) {

        if (BigDecimalUtils.is(balance).greaterThan(balanceLimit)) {
            throw new IllegalArgumentException("보유 금액은 최대 보유 금액보다 작거나 같아야 합니다.");
        }
    }

    /**
     * 유저의 한 번에 사용할 수 있는 금액, 하루에 사용할 수 있는 금액, 한 달에 사용할 수 있는 금액을 검증합니다.
     *
     * @param onceLimit    한 번에 사용할 수 있는 금액
     * @param dailyLimit   하루에 사용할 수 있는 금액
     * @param monthlyLimit 한 달에 사용할 수 있는 금액
     */
    private void validateLimit(BigDecimal onceLimit, BigDecimal dailyLimit, BigDecimal monthlyLimit) {

        if (BigDecimalUtils.is(onceLimit).greaterThan(dailyLimit)) {
            throw new IllegalArgumentException("한 번에 사용할 수 있는 금액은 하루에 사용할 수 있는 금액보다 작거나 같아야 합니다.");
        }

        if (BigDecimalUtils.is(dailyLimit).greaterThan(monthlyLimit)) {
            throw new IllegalArgumentException("하루에 사용할 수 있는 금액은 한 달에 사용할 수 있는 금액보다 작거나 같아야 합니다.");
        }
    }
}
