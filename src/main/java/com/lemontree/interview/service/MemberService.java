package com.lemontree.interview.service;

import com.lemontree.interview.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
