package com.lemontree.interview.service;

import com.lemontree.interview.entity.Member;
import com.lemontree.interview.exception.member.MemberNotFoundException;
import com.lemontree.interview.repository.MemberRepository;
import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
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


    /**
     * 유저를 생성합니다.
     *
     * @param request 유저 생성 요청 DTO
     */
    @Transactional
    public Long createMember(MemberCreate request) {

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
}
