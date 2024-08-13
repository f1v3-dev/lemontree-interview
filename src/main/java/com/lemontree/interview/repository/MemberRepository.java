package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 유저 레포지토리 인터페이스 입니다.
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

    /**
     * 유저 ID로 유저 정보를 조회합니다. (비관적 락 사용)
     *
     * @param id 유저 ID
     * @return 유저 정보
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findWithPessimisticLockById(Long id);
}
