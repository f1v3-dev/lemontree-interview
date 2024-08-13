package com.lemontree.interview.repository;

import com.lemontree.interview.entity.Trade;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

/**
 * 거래 레포지토리 인터페이스 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * 결제 ID로 결제 정보를 조회합니다. (비관적 락 사용)
     *
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findWithPessimisticLockById(Long paymentId);
}
