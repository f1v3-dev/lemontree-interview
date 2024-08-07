package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Member JPA Repository.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 모든 유저의 일일 누적 금액(daily_accumulate)을 0으로 초기화합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Member m SET m.dailyAccumulate = 0")
    void resetDailyLimit();


    /**
     * 모든 유저의 월간 누적 금액(monthly_accumulate)을 0으로 초기화합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Member m SET m.monthlyAccumulate = 0")
    void resetMonthlyLimit();
}
