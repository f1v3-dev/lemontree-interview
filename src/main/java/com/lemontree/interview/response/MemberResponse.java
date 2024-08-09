package com.lemontree.interview.response;

import com.lemontree.interview.entity.Member;
import lombok.Getter;

import java.math.BigDecimal;

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
    private final BigDecimal balance;
    private final BigDecimal onceLimit;
    private final BigDecimal dailyLimit;
    private final BigDecimal monthlyLimit;
    private final BigDecimal dailyAccumulate;
    private final BigDecimal monthlyAccumulate;
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
