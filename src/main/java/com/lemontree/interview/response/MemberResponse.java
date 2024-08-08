package com.lemontree.interview.response;

import com.lemontree.interview.entity.Member;
import lombok.Getter;

/**
 * 회원 응답 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@Getter
public class MemberResponse {

    private final Long memberId;
    private final String name;
    private final Long balance;
    private final Long onceLimit;
    private final Long dailyLimit;
    private final Long monthlyLimit;
    private final Long dailyAccumulate;
    private final Long monthlyAccumulate;
    private final Boolean isDeleted;

    public MemberResponse(final Member member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.balance = member.getBalance();
        this.onceLimit = member.getOnceLimit();
        this.dailyLimit = member.getDailyLimit();
        this.monthlyLimit = member.getMonthlyLimit();
        this.dailyAccumulate = member.getDailyAccumulate();
        this.monthlyAccumulate = member.getMonthlyAccumulate();
        this.isDeleted = member.getIsDeleted();
    }
}
